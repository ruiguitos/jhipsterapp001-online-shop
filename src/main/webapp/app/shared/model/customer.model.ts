import { IWishList } from '@/shared/model/wish-list.model';
import { IAddress } from '@/shared/model/address.model';

export interface ICustomer {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  telephone?: string | null;
  wishLists?: IWishList[] | null;
  addresses?: IAddress[] | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public telephone?: string | null,
    public wishLists?: IWishList[] | null,
    public addresses?: IAddress[] | null
  ) {}
}
