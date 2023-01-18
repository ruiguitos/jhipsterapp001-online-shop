<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsterapp001App.customer.home.createOrEditLabel"
          data-cy="CustomerCreateUpdateHeading"
          v-text="$t('jhipsterapp001App.customer.home.createOrEditLabel')"
        >
          Create or edit a Customer
        </h2>
        <div>
          <div class="form-group" v-if="customer.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.customer.firstName')" for="customer-firstName"
              >First Name</label
            >
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="customer-firstName"
              data-cy="firstName"
              :class="{ valid: !$v.customer.firstName.$invalid, invalid: $v.customer.firstName.$invalid }"
              v-model="$v.customer.firstName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.customer.lastName')" for="customer-lastName">Last Name</label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="customer-lastName"
              data-cy="lastName"
              :class="{ valid: !$v.customer.lastName.$invalid, invalid: $v.customer.lastName.$invalid }"
              v-model="$v.customer.lastName.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.customer.email')" for="customer-email">Email</label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="customer-email"
              data-cy="email"
              :class="{ valid: !$v.customer.email.$invalid, invalid: $v.customer.email.$invalid }"
              v-model="$v.customer.email.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.customer.telephone')" for="customer-telephone">Telephone</label>
            <input
              type="text"
              class="form-control"
              name="telephone"
              id="customer-telephone"
              data-cy="telephone"
              :class="{ valid: !$v.customer.telephone.$invalid, invalid: $v.customer.telephone.$invalid }"
              v-model="$v.customer.telephone.$model"
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
            :disabled="$v.customer.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./customer-update.component.ts"></script>
