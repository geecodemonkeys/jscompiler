package jscompiler.ast.visitor;

import java.util.List;
import java.util.Stack;

import jscompiler.ast.ASTArrayConstant;
import jscompiler.ast.ASTAssignmentExpression;
import jscompiler.ast.ASTConstant;
import jscompiler.ast.ASTExpression;
import jscompiler.ast.ASTFunction;
import jscompiler.ast.ASTFunctionCall;
import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTFunctionExpression;
import jscompiler.ast.ASTIdentifier;
import jscompiler.ast.ASTIfStatement;
import jscompiler.ast.ASTIndexExpression;
import jscompiler.ast.ASTNameValuePair;
import jscompiler.ast.ASTNewExpression;
import jscompiler.ast.ASTNode;
import jscompiler.ast.ASTObjectConst;
import jscompiler.ast.ASTPostfixExpression;
import jscompiler.ast.ASTPropertyAccess;
import jscompiler.ast.ASTReturn;
import jscompiler.ast.ASTRoot;
import jscompiler.ast.ASTTernaryCondition;
import jscompiler.ast.ASTTwoOperandExpression;
import jscompiler.ast.ASTUnaryExpression;
import jscompiler.ast.ASTVariableDeclaration;
import jscompiler.ast.ASTWhile;
import jscompiler.execution.Context;
import jscompiler.execution.NativeFunctions;
import jscompiler.execution.RefUtil;
import jscompiler.execution.ScopeFrame;
import jscompiler.execution.operators.Operators;
import jscompiler.jsobject.JsArray;
import jscompiler.jsobject.JsBoolean;
import jscompiler.jsobject.JsFunction;
import jscompiler.jsobject.JsNull;
import jscompiler.jsobject.JsNumber;
import jscompiler.jsobject.JsObject;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsReference;
import jscompiler.jsobject.JsString;
import jscompiler.jsobject.JsUndefined;
import jscompiler.util.ErrorMessages;

//TODO for optimization purpose, when a function does not use free variables, 
//implementations may not to save a parent scope chain
public class SimpleExecutionVisitor extends ASTVisitor {
	
	public Context context = new Context();
	private Stack<JsObjectBase> stack = null;
	private boolean skipNextId = false;
	private boolean isReturnHit = false;
	private JsObject globalObject;
	private JsObject objectPrototype = new JsObject(null);

	
	public SimpleExecutionVisitor() {
		stack = new Stack<JsObjectBase>();
		context.pushScope(new ScopeFrame());
	}

    private JsFunction getFunctionPrototype() {
        JsReference globalFunction = (JsReference)context.getScopes().get(0).getVariables().get(1);
        JsFunction function = (JsFunction) globalFunction.getValue();
        return (JsFunction)function.getProp(JsObject.PROTOTYPE_EXPLICIT);
    }
	
	@Override
	public boolean visit(ASTRoot root) { 
		
		//add global object
        addGlobalObjects();
        
        //this value
        context.setThisValue(globalObject);
		
		//adds the function declarations so that we can call
		//them before declaring them
		addFunctionDeclarations(context.peek(), root.getNodes());
		VarFinderVisitor varFinder = new VarFinderVisitor(context.peek());
		for (ASTNode node : root.getNodes()) {
			node.accept(varFinder);
		}
		updateScopeOfInnerFunctions(context);
		return true;
	}

    private void addGlobalObjects() {
    	
    	globalObject = new JsObject(objectPrototype);
    	
        //TODO fix the depth and offset indexes all for the global object
        context.peek().addVarReference(new JsReference(
                "global", 0, 0,0, globalObject));

        JsFunction globalFunction =  new JsFunction(null, objectPrototype);
        globalFunction.setProp(JsObject.PROTO, globalObject);
        JsFunction functionPrototype =  new JsFunction(null, objectPrototype);
        globalFunction.setProp(JsObject.PROTOTYPE_EXPLICIT, functionPrototype);

        context.peek().addVarReference(new JsReference("Function", 0, 0, 0, globalFunction));
    }

    /**
	 * The last call of the execution. Inflate the variables 
	 */
	@Override
	public void endVisit(ASTRoot node) {
		if (stack.isEmpty()) {
			stack.add(new JsUndefined());
		}
	}
	
	@Override
	public void endVisit(ASTConstant node) {
		switch (node.getConstType()) {
			case NUMBER_CONSTANT:
				stack.add(
						new JsNumber(
								Double.parseDouble(node.getValue())));
				break;
			case STRING_CONSTANT:
				stack.add(new JsString(node.getValue()));
				break;
			case FALSE_CONST:
				stack.add(new JsBoolean(false));
				break;
			case TRUE_CONST:
				stack.add(new JsBoolean(true));
				break;
			case NULL:
				stack.add(new JsNull());
				break;
		default:
			break;
		
		}
	}
	
	@Override
	public boolean visit(ASTNameValuePair node) {		
		if (node.getExpression() != null) {			
			node.getExpression().accept(this);
		}
		return false;
	}
	
	@Override
	public void endVisit(ASTObjectConst node) {
		JsObject obj = new JsObject(globalObject);
		for (int i = node.getFields().size() - 1 ; i >= 0; i--) {
			ASTNameValuePair pair = node.getFields().get(i);
			JsObjectBase value = stack.pop();
			
			//i don't know if here to dereference a reference
			value = RefUtil.getValue(value);
			//-------------------------------------------
			if (pair.getIdentifier() != null) {				
				obj.setProp(pair.getIdentifier().getName(), value);
			} else if (pair.getConstant() != null) {
				
				//TODO just guessing. if the object is {5 : "haha"}, what will happen?  
				obj.setProp(pair.getConstant().getValue(), value);
			} else {
				throw new RuntimeException("Bad field name!");
			}
		}
		stack.push(obj);
	}
	
	@Override
	public boolean visit(ASTPropertyAccess node) {
		if (isReturnHit) {
			return false;
		}
		
		node.getMemberExpression().accept(this);
		JsObjectBase obj = stack.pop();
		if (obj.getCode() == JsObjectBase.REFERENCE) {
			obj = RefUtil.getRefValue((JsReference) obj);
		}
		ASTIdentifier prop = node.getPropertyIdentifier();
		JsReference ref = new JsReference(prop, obj, true);
		stack.push(ref);
		
		return false;
	}

	@Override
	public void endVisit(ASTIdentifier node) {
		if (skipNextId) {
			skipNextId = false;
			return;
		}
		if (node.getName().equals("this")) {
			stack.add(context.getThisValue());
			return;
		}
		
		JsReference ref = new JsReference(node);
		JsReference storedRef = context.lookupGeneric(ref.getId(),
				ref.getScopeDepth(), ref.getOffset(), 
				ref.getCompileTimeDepth());
		if (storedRef == null) {
			storedRef = searchPropertyOfGlobalObject(node);
			if (storedRef == null) {
				if (!NativeFunctions.isNativeFunctionOrClass(node.getName())) {					
					throw new RuntimeException(ErrorMessages.REFERENCE_NOT_DEFINED + 
							" : " + node.getName());
				}
				storedRef = ref;
			}
		}
		storedRef.setOffset(ref.getOffset());
		storedRef.setScopeDepth(ref.getScopeDepth());
		storedRef.setCompileTimeDepth(ref.getCompileTimeDepth());
		stack.add(storedRef);
	}
	
	private JsReference searchPropertyOfGlobalObject(ASTIdentifier node) {
		JsObjectBase value = RefUtil.lookupPropValue(globalObject, node.getName());
		if (value.getCode() == JsObjectBase.UNDEFINED) {
			return null;
		}
		return new JsReference(node, value);
	}

	@Override
	public void endVisit(ASTVariableDeclaration node) {
		JsObjectBase arg = stack.pop();
		
		ASTIdentifier id = node.getIdentifier();
		//4. put the var reference in the scope
		context.updateReference(generateRef(id.getName(), 
				id.getScopeDepth(), id.getOffset(), 
				id.getRealScopeDepth() ,arg));
	}
	
	@Override
	public boolean visit(ASTVariableDeclaration node) {
		if (isReturnHit) {
			return false;
		}
		if (node.getInitializer() == null) {	
			ASTIdentifier id = node.getIdentifier();
			context.updateReference(generateRef(id.getName(), 
					id.getScopeDepth(), id.getOffset() , 
					id.getRealScopeDepth(),
					new JsUndefined()));
			
			//supper important!!! else it will hit endVisit
			return false;
		}
		skipNextId = true;
		return true;
	}
	
	@Override
	public boolean visit(ASTNewExpression node) {
		if (isReturnHit) {
			return false;
		}
		node.getMemberExpression().accept(this);
		
		JsObjectBase poped = stack.pop();
		JsObjectBase function = null;
		if (poped.getCode() == JsObjectBase.REFERENCE) {
			function = RefUtil.getRefValue((JsReference) poped);
		}
		if (function.getCode() != JsObjectBase.FUNCTION) {
			throw new RuntimeException("Not a function in new!");
		}
		
		JsFunction constructor = (JsFunction) function;
		JsObject newObject = null;
		JsObjectBase prototype = constructor.getProp(JsObject.PROTOTYPE_EXPLICIT);
		if (prototype.getCode() == JsObjectBase.OBJECT) {
			newObject = new JsObject(prototype);
		} else {
			newObject = new JsObject(objectPrototype);
		}
		
		if (node.getArguments() != null) {
			for (ASTExpression expr : node.getArguments()) {
				expr.accept(this);
			}
		}
		int argCount = node.getArguments().size();
		JsObjectBase[] arguments = popArguments(argCount);
		callFunction(arguments, constructor, newObject);
		JsObjectBase retValue = stack.pop();
		if (retValue.getCode() == JsObjectBase.OBJECT) {
			stack.push(retValue);
		} else {
			stack.push(newObject);
		}
		return false;
	}
	
	@Override
	public boolean visit(ASTFunctionExpression node) {
		//may or may not add the name of the function as a reference to
				//to itself
		Context ctx = context.clone();
		JsFunction funct = new JsFunction(node, ctx, getFunctionPrototype());
		
		//the A.prototype property
		JsObject object = new JsObject(objectPrototype);
		object.setProp(JsObject.CONSTRUCTOR, funct);
		funct.setProp(JsObject.PROTOTYPE_EXPLICIT, object);
		
		stack.push(funct);
		
		//we do not step deep into the function expression ast
		//cause this will mess the stack, it needs to be called
		//in ASTFunctionCall node
		return false;
	}
	
	@Override
	public boolean visit(ASTFunctionCall node) {
		if (isReturnHit) {
			return false;
		}
		return true;
	}
	
	private JsObjectBase[] popArguments(int argCount) {
		JsObjectBase[] arguments = new JsObjectBase[argCount];
		for (int i = argCount - 1 ; i >= 0; i--) {
			JsObjectBase obj = stack.pop();
			arguments[i] = obj;
		}
		return arguments;
	}
	
	private JsObjectBase getRefBase(JsReference ref) {
		if (ref.isPropReference()) {
			return ref.getValue();
		}
		
		int i = context.getBase(ref.getId());
		if (i == 0 || i == context.getScopes().size() - 1) {
			return globalObject;
		}
		
		return null;
		
	}
	
	//does not handle when the function is called with less arguments
	//that it is declared
	@Override
	public void endVisit(ASTFunctionCall node) {
		int argCount = node.getArguments().size();
		JsObjectBase[] arguments = popArguments(argCount);
		JsObjectBase obj = stack.pop();
		JsObjectBase thisObject = globalObject;
		if (obj.getCode() == JsObjectBase.REFERENCE) {
			JsReference ref = (JsReference) obj;
			thisObject = getRefBase(ref);
		}
		callFunction(arguments, obj, thisObject);
	}

	private void callFunction(JsObjectBase[] arguments, JsObjectBase ref, 
			JsObjectBase thisValue) {
		
		if (ref.getCode() == JsObjectBase.FUNCTION) {
			executeFunc((JsFunction) ref, arguments, thisValue);
			return;
		}
		if (ref.getCode() == JsObjectBase.REFERENCE) {
			JsObjectBase value = RefUtil.getRefValue((JsReference)ref);
			if (NativeFunctions.isNativeFunctionOrClass(((JsReference)ref).getId())) {
				executeNativeFunction(((JsReference)ref).getId(), arguments);
				return;
			}
			if (value.getCode() != JsObjectBase.FUNCTION) {
				throw new RuntimeException(ErrorMessages.NOT_A_FUNCTION);
			}
			executeFunc((JsFunction) value, arguments, thisValue);
			return;
		}
		throw new RuntimeException(ErrorMessages.NOT_A_FUNCTION);
	}
	
	private void executeNativeFunction(String id, JsObjectBase[] arguments) {
		//Method method = NativeFunctions.getFunctions().get(id);
		Object[] args = new Object[arguments.length];
		for (int i = 0; i < args.length; i++) {
			if (arguments[i].getCode() == JsObjectBase.STRING) {
				args[i] = ((JsString)arguments[i]).getValue();
			} else {
				args[i] = arguments[i].getRealValue();
			}
		}
		System.out.println(args[0]);
		/*try {
			method.invoke(null, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}*/
		
	}

	//TODO think for the case where the supplied args are less than
	//the desired one
	private void executeFunc(JsFunction func, JsObjectBase[] arguments, 
			JsObjectBase thisValue) {

		ASTFunction function = func.getFunction();
		ScopeFrame scope = new ScopeFrame();
		
		if (function.getCode() == ASTFunction.EXPRESSION &&
				function.getName() != null) {			
			JsReference functionRef = new JsReference(function.getName(), 
					new JsFunction(function, getFunctionPrototype()));
			scope.addVarReference(functionRef);
		}
		
		List<ASTIdentifier> args = function.getArguments();
		for (int i = 0; i < args.size(); i++) {
			JsObjectBase argValue = null;
			if (arguments.length < i + 1) {
				argValue = new JsUndefined();
			} else {
				if (arguments[i].getCode() == JsObjectBase.REFERENCE) {
					argValue = RefUtil.getRefValue((JsReference)arguments[i]);
				} else {					
					argValue = arguments[i];
				}
			}
			JsReference argRef =  new JsReference(args.get(i), argValue);
		//1. add arguments in the scope
			scope.addVarReference(argRef);
		}
		
		//2. add function declarations cause we can call the function before
		//it is defined
		addFunctionDeclarations(scope, 
				function.getBody());
		scope.setFunction(function);
		
		//3.add all variables defined with var keyword with val undefined
		VarFinderVisitor varFinder = new VarFinderVisitor(scope);
		for (ASTNode node : function.getBody()) {
			node.accept(varFinder);
		}
		
		//add activation object
		func.getScope().pushScope(scope);
		
		//update the scope chain of the first level nested functions
		//for correct closure
		updateScopeOfInnerFunctions(func.getScope());
		
		Context contextBeforeExecution = context;
		context = func.getScope();
		context.setThisValue(thisValue);
		
		//the real call
		//function.accept(this);
		for (ASTNode node : function.getBody()) {
			node.accept(this);
			if (isReturnHit) {
				break;
			}
		}
		isReturnHit = false;
		if (context.peek().getReturnValue() == null) {
			stack.push(new JsUndefined());
		}
		
		//remove activation object
		func.getScope().popScope();
		context = contextBeforeExecution; 
	}
	
	private void updateScopeOfInnerFunctions(Context scope) {
		List<JsReference> refList = scope.peek().getVariables();
		for (JsReference ref : refList) {
			if (ref.getValue() != null) {
				JsObjectBase refValue = RefUtil.getRefValue(ref);
				if (refValue.getCode() == JsObjectBase.FUNCTION) {
					JsFunction func = (JsFunction) ref.getValue();
					func.setScope(scope.clone());
				}
			}
		}
		
	}

	@Override
	public boolean visit(ASTReturn node) {
		if (isReturnHit) {
			return false;
		}
		return super.visit(node);
	}
	
	@Override
	public void endVisit(ASTReturn node) {
		ASTExpression expr = node.getExpression();
		if (expr != null) {
			ScopeFrame scope = context.peek();
			JsObjectBase obj = stack.peek();
			scope.setReturnValue(obj);
		}
		isReturnHit = true;
	}
	
	@Override
	public boolean visit(ASTIfStatement node) {
		if (isReturnHit) {
			return false;
		}
		node.getCondition().accept(this);
		JsObjectBase base = stack.pop();
		if (Operators.toBoolean(base).getValue()) {
			node.getIfBody().accept(this);
		} else {
			if (node.getElseBody() != null) {
				node.getElseBody().accept(this);
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(ASTTernaryCondition node) {
		if (isReturnHit) {
			return false;
		}
		node.getCondition().accept(this);
		JsObjectBase base = stack.pop();
		if (Operators.toBoolean(base).getValue()) {
			node.getTrueExpression().accept(this);
		} else {
			if (node.getFalseExpression() != null) {
				node.getFalseExpression().accept(this);
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(ASTWhile node) {
		if (isReturnHit) {
			return false;
		}
		
		JsObjectBase retVal = null;
		while (true) {
			if (isReturnHit) {
				break;
			}
			node.getCondition().accept(this);
			JsObjectBase base = stack.pop();
			JsBoolean boolValue = Operators.toBoolean(base);
			int stackSize = stack.size();
			if (!boolValue.getValue()) {				
				break;
			}
			node.getBody().accept(this);
			
			int stackSizeIncrease =  stack.size() - stackSize;
			if (stackSizeIncrease > 0) {
				retVal = stack.pop();
				retVal = RefUtil.getValue(retVal);
			}
		}
		
		if (retVal != null) {
			stack.push(retVal);
		}
		
		return false;
	}
	
	@Override
	public boolean visit(ASTAssignmentExpression node) {
		if (isReturnHit) {
			return false;
		}
		
		JsObjectBase left = null;
		if (node.getLeftHandSide() instanceof ASTIdentifier) {
			ASTIdentifier id = (ASTIdentifier) node.getLeftHandSide();
			if ("this".equals(id.getName())) {
				throw new RuntimeException(ErrorMessages.BAD_LEFT_HAND_SIDE);
			}
			JsReference ref = context.lookupGeneric(id.getName(), 
					id.getScopeDepth(), id.getOffset(), id.getRealScopeDepth());
			if (ref != null) {
				left = ref;
			} else {
				left = new JsReference(id, globalObject, true);
			}
		} else {			
			node.getLeftHandSide().accept(this);
			left = stack.pop();
		}
		
		if (left.getCode() != JsObjectBase.REFERENCE) {
			throw new RuntimeException(ErrorMessages.BAD_LEFT_HAND_SIDE);
		}
		if (node.getLeftHandSide() instanceof ASTFunctionCall) {
			throw new RuntimeException(ErrorMessages.BAD_LEFT_HAND_SIDE);
		}
		node.getRightHandSide().accept(this);
		JsObjectBase right = stack.pop();
		stack.push(RefUtil.getValue(right));
		
		JsReference ref = (JsReference) left;
		if (ref.isPropReference()) {
			if (right.getCode() == JsObjectBase.REFERENCE) {				
				JsReference valRef = (JsReference) right;
				right = RefUtil.getRefValue(valRef);
			}
			RefUtil.setPropValue(ref, ref.getId(), right);
			return false;
		}
		//FIXME will not work if the reference has no identifier
		JsReference refSecond = generateRef(ref.getId(), 
				ref.getScopeDepth(), ref.getOffset(), 
				ref.getCompileTimeDepth(), right);
		context.updateReference(refSecond);
		return false;
	}

	/**
	 * Searches for all function declarations directly into the 
	 * body
	 * @param scope
	 * @param list
	 */
	private void addFunctionDeclarations(ScopeFrame scope,
			List<ASTNode> list) {
		for (ASTNode node : list) {
			if (node instanceof ASTFunctionDeclaration) {
				ASTFunctionDeclaration function = (ASTFunctionDeclaration) node;
				JsFunction funct = new JsFunction(function, getFunctionPrototype());
				
				//the A.prototype property
				JsObject object = new JsObject(objectPrototype);
				object.setProp(JsObject.CONSTRUCTOR, funct);
				funct.setProp(JsObject.PROTOTYPE_EXPLICIT, object);
				
				JsReference functionRef = new JsReference(
						function.getName(), 
						funct);
				scope.addVarReference(functionRef);
			}
		}
	}

	@Override
	public boolean visit(ASTFunctionDeclaration node) {
		return false;
	}
	
	@Override
	public void endVisit(ASTArrayConstant node) {
		if(node.getElements() != null) {
			JsArray array = new JsArray(objectPrototype);
			for (int i = node.getElements().size() - 1; i >= 0; i--) {
				array.addValue(i, stack.pop());
			}
			stack.push(array);
		}
	}
	
	@Override
	public boolean visit(ASTIndexExpression node) {
		if (isReturnHit) {
			return false;
		}
		
		node.getMemberExpression().accept(this);
		JsObjectBase obj = stack.pop();
		if (obj.getCode() == JsObjectBase.REFERENCE) {
			obj = RefUtil.getRefValue((JsReference) obj);
		}
		node.getIndexExpression().accept(this);
		JsObjectBase index = stack.pop();
		String key = "";
		if (index.getCode() == JsObjectBase.NUMBER) {
			key = String.valueOf((int)((JsNumber) index).getValue());
		} else {
			if (index.getCode() == JsObjectBase.STRING) {
				key = ((JsString) index).getValue();
			} else {
				JsObjectBase idx = RefUtil.getValue(index);
				if (idx.getCode() == JsObjectBase.NUMBER) {
					key = String.valueOf((int)((JsNumber) idx).getValue());
				} else {
					if (idx.getCode() == JsObjectBase.STRING) {
						key = ((JsString) idx).getValue();
					} else {
						throw new RuntimeException("Key value is neither number nor a string!");
					}
				}
			}
		}
		ASTIdentifier prop = new ASTIdentifier(key);
		JsReference ref = new JsReference(prop, obj, true);
		stack.push(ref);
		
		return false;
	}
	
	
	private JsReference generateRef(String id, int depth, int offset, 
			int compileTimeDepth,
			JsObjectBase arg) {
		
		JsReference ref = new JsReference(id, depth, offset, compileTimeDepth);
		
		//Important the reference value resolving happens here
		if (JsObjectBase.REFERENCE == arg.getCode()) {
			JsReference valRef = (JsReference) arg;
			ref.setValue(RefUtil.getRefValue(valRef));
		} else {			
			ref.setValue(arg);
		}
		return ref;
	}

	@Override
	public boolean visit(ASTTwoOperandExpression node) {
		if (isReturnHit) {
			return false;
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(ASTTwoOperandExpression node) {

		//don't change
  		JsObjectBase right = stack.pop();
		JsObjectBase left = stack.pop();
		
		JsNumber leftNum = null;
		JsNumber rightNum = null;
		JsObjectBase res = null;
		switch (node.getOperation()) {
			case PLUS:
				res = Operators.add(left, right);
				stack.add(res);
				break;
			case MINUS:
				res = Operators.substract(left, right);
				stack.add(res);
				break;
			case MULTIPLICATION:
				res = Operators.multiply(left, right);
				stack.add(res);
				break;
			case DIVISION:
				res = Operators.divide(left, right);
				stack.add(res);
				break;
			case MODULUS:
				res = Operators.modulus(left, right);
				stack.add(res);
				break;
			case LESS:
				res = Operators.lessThan(left, right);
				stack.add(res);
				break;
			case LESS_OR_EQ:
				res = Operators.lessOrEq(left, right);
				stack.add(res);
				break;
			case GREATER:
				res = Operators.greaterThan(left, right);
				stack.add(res);
				break;
			case GREATER_OR_EQ:
				res = Operators.greaterOrEq(left, right);
				stack.add(res);
				break;
			case EQUALS_COMPARISON:
				leftNum = unpack( left);
				rightNum = unpack(right);
				stack.add(new JsBoolean(
						leftNum.getValue() == rightNum.getValue()));
				break;
			case EQUALS_SPECIAL:
				res = Operators.strictEquals(left, right);
				stack.add(res);
				break;
			case NOT_EQUALS_SPECIAL:
				res = Operators.strictEquals(left, right);
				JsBoolean bool = new JsBoolean(!((JsBoolean)res).getValue());
				stack.add(bool);
				break;
			case NOT_EQUALS:
				leftNum = unpack( left);
				rightNum = unpack(right);
				stack.add(new JsBoolean(
						leftNum.getValue() != rightNum.getValue()));
				break;
			case AND:
				res = Operators.logicalAnd(left, right);
				stack.add(res);
				break;
			case OR:
				res = Operators.logicalOr(left, right);
				stack.add(res);
				break;
		default:
			break;
		}
	}
	
	@Override
	public void endVisit(ASTUnaryExpression node) {
		JsObjectBase value = null;
		value = stack.pop();
		switch (node.getOpType()) {
			case MINUS:
				value = Operators.unaryMinus(value);
				stack.push(value);
				break;
			case UNARY_NEGATE:
				value = Operators.logicalNot(value);
				stack.push(value);
				break;
			default:
				break;
		
		}
	}
	
	@Override
	public boolean visit(ASTUnaryExpression node) {
		if (isReturnHit) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(ASTPostfixExpression node) {
		if (isReturnHit) {
			return false;
		}
		return true;
	}
	
	@Override
	public void endVisit(ASTPostfixExpression node) {
		//TODO add check for arguments and eval
		JsObjectBase top = stack.pop();
		double doubleVal = Operators.toNumber(RefUtil.getValue(top));
		JsNumber oldValue = new JsNumber(doubleVal);
		JsObjectBase newVal = null;
		switch (node.getOpType()) {
			case INCREMENT:
				newVal = Operators.add(oldValue, new JsNumber(1));
				break;
			case DECREMENT:
				newVal = Operators.substract(oldValue, new JsNumber(1));
				break;
			default:
				break;
			
		}
		
		stack.push(oldValue);
		
		if (top.getCode() != JsObjectBase.REFERENCE) {
			return;
		}
		
		JsReference ref = (JsReference) top;
		if (ref.isPropReference()) {
			RefUtil.setPropValue(ref, ref.getId(), newVal);
			return;
		}
		//FIXME will not work if the reference has no identifier
		JsReference refSecond = generateRef(ref.getId(), 
				ref.getScopeDepth(), ref.getOffset(), 
				ref.getCompileTimeDepth(), newVal);
		context.updateReference(refSecond);
		
	}


	private JsNumber unpack(JsObjectBase left) {
		if (left.getCode() == JsObjectBase.NUMBER) {
			return (JsNumber) left;
		}
		
		if (left.getCode() == JsObjectBase.REFERENCE) {
			//return context.lookup((JsReference)left);
			return (JsNumber) RefUtil.getRefValue((JsReference)left);
		}
		return null;
	}
	
	public Stack<JsObjectBase> getStack() {
		return stack;
	}

	public void setStack(Stack<JsObjectBase> stack) {
		this.stack = stack;
	}

}
