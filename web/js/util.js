function isThing(thing){
    return typeof thing !== 'undefined';
}

function randomInt(){
    var min = isThing(arguments[0]) ? arguments[0] : 1;
    var max = isThing(arguments[1]) ? arguments[1] : 900000001;
    if(max < min){
        var temp = max;
        max = min;
        min = temp;
    }

    return Math.floor(Math.random() * (max - min + 1)) + 1;
}
