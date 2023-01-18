<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsterapp001App.wishList.home.createOrEditLabel"
          data-cy="WishListCreateUpdateHeading"
          v-text="$t('jhipsterapp001App.wishList.home.createOrEditLabel')"
        >
          Create or edit a WishList
        </h2>
        <div>
          <div class="form-group" v-if="wishList.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="wishList.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.wishList.title')" for="wish-list-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="wish-list-title"
              data-cy="title"
              :class="{ valid: !$v.wishList.title.$invalid, invalid: $v.wishList.title.$invalid }"
              v-model="$v.wishList.title.$model"
              required
            />
            <div v-if="$v.wishList.title.$anyDirty && $v.wishList.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.wishList.title.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.wishList.restricted')" for="wish-list-restricted"
              >Restricted</label
            >
            <input
              type="checkbox"
              class="form-check"
              name="restricted"
              id="wish-list-restricted"
              data-cy="restricted"
              :class="{ valid: !$v.wishList.restricted.$invalid, invalid: $v.wishList.restricted.$invalid }"
              v-model="$v.wishList.restricted.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.wishList.customer')" for="wish-list-customer">Customer</label>
            <select class="form-control" id="wish-list-customer" data-cy="customer" name="customer" v-model="wishList.customer">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="wishList.customer && customerOption.id === wishList.customer.id ? wishList.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.id }}
              </option>
            </select>
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
            :disabled="$v.wishList.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./wish-list-update.component.ts"></script>
