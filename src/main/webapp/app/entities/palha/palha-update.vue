<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsterapp001App.palha.home.createOrEditLabel"
          data-cy="PalhaCreateUpdateHeading"
          v-text="$t('jhipsterapp001App.palha.home.createOrEditLabel')"
        >
          Create or edit a Palha
        </h2>
        <div>
          <div class="form-group" v-if="palha.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="palha.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.palha.julia')" for="palha-julia">Júlia Palha</label>
            <input
              type="text"
              class="form-control"
              name="julia"
              id="palha-julia"
              data-cy="julia"
              :class="{ valid: !$v.palha.julia.$invalid, invalid: $v.palha.julia.$invalid }"
              v-model="$v.palha.julia.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.palha.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./palha-update.component.ts"></script>
