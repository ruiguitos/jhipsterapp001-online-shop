import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import ProductService from '@/entities/product/product.service';
import { IProduct } from '@/shared/model/product.model';

import CustomerService from '@/entities/customer/customer.service';
import { ICustomer } from '@/shared/model/customer.model';

import { IWishList, WishList } from '@/shared/model/wish-list.model';
import WishListService from './wish-list.service';

const validations: any = {
  wishList: {
    title: {
      required,
    },
    restricted: {},
  },
};

@Component({
  validations,
})
export default class WishListUpdate extends Vue {
  @Inject('wishListService') private wishListService: () => WishListService;
  @Inject('alertService') private alertService: () => AlertService;

  public wishList: IWishList = new WishList();

  @Inject('productService') private productService: () => ProductService;

  public products: IProduct[] = [];

  @Inject('customerService') private customerService: () => CustomerService;

  public customers: ICustomer[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.wishListId) {
        vm.retrieveWishList(to.params.wishListId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.wishList.id) {
      this.wishListService()
        .update(this.wishList)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsterapp001App.wishList.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.wishListService()
        .create(this.wishList)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsterapp001App.wishList.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveWishList(wishListId): void {
    this.wishListService()
      .find(wishListId)
      .then(res => {
        this.wishList = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.productService()
      .retrieve()
      .then(res => {
        this.products = res.data;
      });
    this.customerService()
      .retrieve()
      .then(res => {
        this.customers = res.data;
      });
  }
}
