import { IWishList } from '@/shared/model/wish-list.model';
import { ICategory } from '@/shared/model/category.model';

export interface IProduct {
  id?: number;
  title?: string;
  keywords?: string | null;
  description?: string | null;
  rating?: number | null;
  dateAdded?: Date | null;
  dateModified?: Date | null;
  wishList?: IWishList | null;
  categories?: ICategory[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public title?: string,
    public keywords?: string | null,
    public description?: string | null,
    public rating?: number | null,
    public dateAdded?: Date | null,
    public dateModified?: Date | null,
    public wishList?: IWishList | null,
    public categories?: ICategory[] | null
  ) {}
}
