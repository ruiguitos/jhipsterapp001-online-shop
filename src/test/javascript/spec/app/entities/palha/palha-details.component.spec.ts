/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import PalhaDetailComponent from '@/entities/palha/palha-details.vue';
import PalhaClass from '@/entities/palha/palha-details.component';
import PalhaService from '@/entities/palha/palha.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Palha Management Detail Component', () => {
    let wrapper: Wrapper<PalhaClass>;
    let comp: PalhaClass;
    let palhaServiceStub: SinonStubbedInstance<PalhaService>;

    beforeEach(() => {
      palhaServiceStub = sinon.createStubInstance<PalhaService>(PalhaService);

      wrapper = shallowMount<PalhaClass>(PalhaDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { palhaService: () => palhaServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundPalha = { id: 123 };
        palhaServiceStub.find.resolves(foundPalha);

        // WHEN
        comp.retrievePalha(123);
        await comp.$nextTick();

        // THEN
        expect(comp.palha).toBe(foundPalha);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundPalha = { id: 123 };
        palhaServiceStub.find.resolves(foundPalha);

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
