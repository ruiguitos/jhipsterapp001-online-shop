/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import WishListUpdateComponent from '@/entities/wish-list/wish-list-update.vue';
import WishListClass from '@/entities/wish-list/wish-list-update.component';
import WishListService from '@/entities/wish-list/wish-list.service';

import ProductService from '@/entities/product/product.service';

import CustomerService from '@/entities/customer/customer.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('WishList Management Update Component', () => {
    let wrapper: Wrapper<WishListClass>;
    let comp: WishListClass;
    let wishListServiceStub: SinonStubbedInstance<WishListService>;

    beforeEach(() => {
      wishListServiceStub = sinon.createStubInstance<WishListService>(WishListService);

      wrapper = shallowMount<WishListClass>(WishListUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          wishListService: () => wishListServiceStub,
          alertService: () => new AlertService(),

          productService: () =>
            sinon.createStubInstance<ProductService>(ProductService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

          customerService: () =>
            sinon.createStubInstance<CustomerService>(CustomerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.wishList = entity;
        wishListServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(wishListServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.wishList = entity;
        wishListServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(wishListServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundWishList = { id: 123 };
        wishListServiceStub.find.resolves(foundWishList);
        wishListServiceStub.retrieve.resolves([foundWishList]);

        // WHEN
        comp.beforeRouteEnter({ params: { wishListId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.wishList).toBe(foundWishList);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
