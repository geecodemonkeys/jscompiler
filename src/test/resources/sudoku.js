var matrix = [
[2, 4, 0, 0, 7, 0, 3, 6, 0],
[0, 6, 0, 3, 8, 0, 1, 0, 0],
[9 ,8 ,0 ,0, 1, 0, 7, 4, 0],
[4 ,0 ,0 ,6 ,0 ,1 ,8, 7, 2],
[0, 0, 0, 7, 9, 2, 0, 0, 0],
[1, 7, 2, 4, 0, 8, 0, 0, 6],
[0, 5, 4, 0, 2, 0, 0, 8, 3],
[0, 0, 6, 0, 4, 3, 0, 9, 0],
[0, 2, 9, 0, 6, 0, 0, 1, 4]
];
var cols = [];
var rows = [];
var subs = [];
var i = 0;
while(i < 9) {
	cols[i] = [0 ,0 ,0, 0, 0, 0, 0 ,0 ,0];
	rows[i] = [0 ,0 ,0, 0, 0, 0, 0 ,0 ,0];
	i++;
}
i = 0;
while (i < 3) {
	subs[i] = [[0 ,0 ,0, 0, 0, 0, 0 ,0 ,0], [0 ,0 ,0, 0, 0, 0, 0 ,0 ,0], [0 ,0 ,0, 0, 0, 0, 0 ,0 ,0]];
	i++;
}
var row = 0;
var num = 0;
while (row < 9) {
	var col = 0;
	while ( col < 9 ) {
		if (matrix[row][col] != 0) {
			num = matrix[row][col];
			rows[row][num - 1] = num;
			cols[col][num - 1] = num;
			subs[row / 3][col / 3][num - 1] = num;
		}
		col++;
	}
	row++;
}
function solve() {
	var i = 0;
	var found = 0;
	var row = 0;
	var col = 0;
	while (i < 9 && found != 1) {
		var j = 0;
		while(j < 9 && found != 1) {
			if (matrix[i][j] === 0) {
				found = 1;
				row = i;
				col = j;
			}
			j++;		
		}
		i++;	
	}
	if (found === 0) {
		return true;	
	}
	i = 1;
	while(i < 10) {
		if (rows[row][i - 1] === 0) {
			if (cols[col][i - 1] === 0) {
				if (subs[row / 3][col / 3][i - 1] === 0) {
					rows[row][i - 1] = i;
					cols[col][i - 1] = i;
					subs[row/3][col / 3][i - 1] = i;
					matrix[row][col] = i;
					//log(matrix);
					var res = solve();
					if (res) {
						return true;
					}
					matrix[row][col] = 0;
					rows[row][i - 1] = 0;
					cols[col][i - 1] = 0;
					subs[row/3][col / 3][i - 1] = 0;
				}
			}
		}
		i++;
	}
	return false;
}
solve();
matrix;
