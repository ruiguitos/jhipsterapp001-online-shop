export interface IPalha {
  id?: number;
  julia?: string | null;
}

export class Palha implements IPalha {
  constructor(public id?: number, public julia?: string | null) {}
}
