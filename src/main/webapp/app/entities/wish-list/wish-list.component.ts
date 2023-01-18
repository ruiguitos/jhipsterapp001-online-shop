import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IWishList } from '@/shared/model/wish-list.model';

import WishListService from './wish-list.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class WishList extends Vue {
  @Inject('wishListService') private wishListService: () => WishListService;
  @Inject('alertService') private alertService: () => AlertService;

  private removeId: number = null;

  public wishLists: IWishList[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllWishLists();
  }

  public clear(): void {
    this.retrieveAllWishLists();
  }

  public retrieveAllWishLists(): void {
    this.isFetching = true;
    this.wishListService()
      .retrieve()
      .then(
        res => {
          this.wishLists = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IWishList): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeWishList(): void {
    this.wishListService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('jhipsterapp001App.wishList.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllWishLists();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
