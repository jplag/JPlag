class AddTest {

    private _x: number;
    protected _y = 5;

    constructor(x) {
        this._x = x;
    }

    public getX() {
        return this._x;
    }

    public setX(x: number) {
        this._x = x;
    }

    get y() {
        return this._y;
    }

    set y(y: number) {
        this._y = y;
    }
}