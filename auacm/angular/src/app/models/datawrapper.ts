export class DataWrapper<T> {
	public data: T;

	constructor(object: T) {
		this.data = object;
	}

	getData(): T {
		return this.data;
	}
}