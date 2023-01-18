/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import WishListComponent from '@/entities/wish-list/wish-list.vue';
import WishListClass from '@/entities/wish-list/wish-list.component';
import WishListService from '@/entities/wish-list/wish-list.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(ToastPlugin);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('WishList Management Component', () => {
    let wrapper: Wrapper<WishListClass>;
    let comp: WishListClass;
    let wishListServiceStub: SinonStubbedInstance<WishListService>;

    beforeEach(() => {
      wishListServiceStub = sinon.createStubInstance<WishListService>(WishListService);
      wishListServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<WishListClass>(WishListComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          wishListService: () => wishListServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      wishListServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllWishLists();
      await comp.$nextTick();

      // THEN
      expect(wishListServiceStub.retrieve.called).toBeTruthy();
      expect(comp.wishLists[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      wishListServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      expect(wishListServiceStub.retrieve.callCount).toEqual(1);

      comp.removeWishList();
      await comp.$nextTick();

      // THEN
      expect(wishListServiceStub.delete.called).toBeTruthy();
      expect(wishListServiceStub.retrieve.callCount).toEqual(2);
    });
  });
});
