import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import CategoryService from './category/category.service';
import ProductService from './product/product.service';
import CustomerService from './customer/customer.service';
import AddressService from './address/address.service';
import WishListService from './wish-list/wish-list.service';
import PalhaService from './palha/palha.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('categoryService') private categoryService = () => new CategoryService();
  @Provide('productService') private productService = () => new ProductService();
  @Provide('customerService') private customerService = () => new CustomerService();
  @Provide('addressService') private addressService = () => new AddressService();
  @Provide('wishListService') private wishListService = () => new WishListService();
  @Provide('palhaService') private palhaService = () => new PalhaService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
