import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import CustomerService from '@/entities/customer/customer.service';
import { ICustomer } from '@/shared/model/customer.model';

import { IAddress, Address } from '@/shared/model/address.model';
import AddressService from './address.service';

const validations: any = {
  address: {
    address1: {},
    address2: {},
    city: {},
    postcode: {
      required,
      maxLength: maxLength(10),
    },
    country: {
      required,
      maxLength: maxLength(2),
    },
  },
};

@Component({
  validations,
})
export default class AddressUpdate extends Vue {
  @Inject('addressService') private addressService: () => AddressService;
  @Inject('alertService') private alertService: () => AlertService;

  public address: IAddress = new Address();

  @Inject('customerService') private customerService: () => CustomerService;

  public customers: ICustomer[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.addressId) {
        vm.retrieveAddress(to.params.addressId);
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
    if (this.address.id) {
      this.addressService()
        .update(this.address)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsterapp001App.address.updated', { param: param.id });
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
      this.addressService()
        .create(this.address)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('jhipsterapp001App.address.created', { param: param.id });
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

  public retrieveAddress(addressId): void {
    this.addressService()
      .find(addressId)
      .then(res => {
        this.address = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.customerService()
      .retrieve()
      .then(res => {
        this.customers = res.data;
      });
  }
}
