import { IProduct } from '@/shared/model/product.model';
import { ICustomer } from '@/shared/model/customer.model';

export interface IWishList {
  id?: number;
  title?: string;
  restricted?: boolean | null;
  products?: IProduct[] | null;
  customer?: ICustomer | null;
}

export class WishList implements IWishList {
  constructor(
    public id?: number,
    public title?: string,
    public restricted?: boolean | null,
    public products?: IProduct[] | null,
    public customer?: ICustomer | null
  ) {
    this.restricted = this.restricted ?? false;
  }
}
