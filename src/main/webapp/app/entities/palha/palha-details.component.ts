import { Component, Vue, Inject } from 'vue-property-decorator';

import { IPalha } from '@/shared/model/palha.model';
import PalhaService from './palha.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class PalhaDetails extends Vue {
  @Inject('palhaService') private palhaService: () => PalhaService;
  @Inject('alertService') private alertService: () => AlertService;

  public palha: IPalha = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.palhaId) {
        vm.retrievePalha(to.params.palhaId);
      }
    });
  }

  public retrievePalha(palhaId) {
    this.palhaService()
      .find(palhaId)
      .then(res => {
        this.palha = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
