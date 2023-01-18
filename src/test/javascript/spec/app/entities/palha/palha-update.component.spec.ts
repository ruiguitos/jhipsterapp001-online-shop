/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import PalhaUpdateComponent from '@/entities/palha/palha-update.vue';
import PalhaClass from '@/entities/palha/palha-update.component';
import PalhaService from '@/entities/palha/palha.service';

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
  describe('Palha Management Update Component', () => {
    let wrapper: Wrapper<PalhaClass>;
    let comp: PalhaClass;
    let palhaServiceStub: SinonStubbedInstance<PalhaService>;

    beforeEach(() => {
      palhaServiceStub = sinon.createStubInstance<PalhaService>(PalhaService);

      wrapper = shallowMount<PalhaClass>(PalhaUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          palhaService: () => palhaServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.palha = entity;
        palhaServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(palhaServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.palha = entity;
        palhaServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(palhaServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundPalha = { id: 123 };
        palhaServiceStub.find.resolves(foundPalha);
        palhaServiceStub.retrieve.resolves([foundPalha]);

        // WHEN
        comp.beforeRouteEnter({ params: { palhaId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.palha).toBe(foundPalha);
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
