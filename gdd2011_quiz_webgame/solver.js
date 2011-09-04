// マウスイベントを生成しておく
var mouseEvent = document.createEvent('MouseEvents');
mouseEvent.initEvent('click', false, true);

// 各パネルの色情報を初期化する
// ちなみにパネルの数は動的に変わる
var colors = new Array()
var index  = 0
while(true) {
	var element = document.getElementById('card' + index);
	if (element == null) {
		break;
	} else {
		element.dispatchEvent(mouseEvent);
		colors[index] = element.style.backgroundColor
	}
	index ++;
}

// 同じ色のパネルにクリックイベントを発生させる
// つまり裏が見えている状態で神経衰弱する
for(i = 0; i < colors.length; i ++) {
	if(colors[i] == null)
		continue;

	var element1 = document.getElementById('card' + i);

	for(j = 0; j < colors.length; j ++) {
		if(i == j)
			continue;

		if(colors[j] == null)
			continue;

		if(colors[i] == colors[j]) {
			var element2 = document.getElementById('card' + j);

			element1.dispatchEvent(mouseEvent);
			element2.dispatchEvent(mouseEvent);

			// めくったパネルはnullを入れておく
			colors[i] = null;
			colors[j] = null;
		}
	}
}

/*
str = ''
for(i = 0; i < 8; i ++) {
	str += colors[i] + '\n'
}

alert('Card colors are\n' + str);
*/
