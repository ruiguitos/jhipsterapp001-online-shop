import { IProduct } from '@/shared/model/product.model';

import { CategoryStatus } from '@/shared/model/enumerations/category-status.model';
export interface ICategory {
  id?: number;
  description?: string;
  sortOrder?: number | null;
  dateAdded?: Date | null;
  dateModified?: Date | null;
  status?: CategoryStatus | null;
  parent?: ICategory | null;
  products?: IProduct[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public description?: string,
    public sortOrder?: number | null,
    public dateAdded?: Date | null,
    public dateModified?: Date | null,
    public status?: CategoryStatus | null,
    public parent?: ICategory | null,
    public products?: IProduct[] | null
  ) {}
}
