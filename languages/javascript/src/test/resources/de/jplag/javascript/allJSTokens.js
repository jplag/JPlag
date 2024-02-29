import {test} from 'test';
class Class {
    #x;

    constructor(x) {
        this.x = x;
    }

    test(y) {
        return this.x + y;
    }
}

let a = 3;
const d = 4;

function c() {
    let b;
    b = 8;
    return a - b;
}

switch (c()) {
    case 1:
        break;
    default:
        console.log(c())
}

try {
    throw "Error"
} catch (e) {
    console.log('Error', e);
} finally {
    console.log()
}

for (let i = 0; i < a; i++) {

}

while(a > 3) {
    a--;
    if (d > 5) {
        continue;
    }
}

export default Class;
export { c };

if (d) {

} else if (d < 10) {

} else {

}