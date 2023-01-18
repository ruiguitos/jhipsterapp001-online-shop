<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="jhipsterapp001App.category.home.createOrEditLabel"
          data-cy="CategoryCreateUpdateHeading"
          v-text="$t('jhipsterapp001App.category.home.createOrEditLabel')"
        >
          Create or edit a Category
        </h2>
        <div>
          <div class="form-group" v-if="category.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="category.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.description')" for="category-description"
              >Description</label
            >
            <input
              type="text"
              class="form-control"
              name="description"
              id="category-description"
              data-cy="description"
              :class="{ valid: !$v.category.description.$invalid, invalid: $v.category.description.$invalid }"
              v-model="$v.category.description.$model"
              required
            />
            <div v-if="$v.category.description.$anyDirty && $v.category.description.$invalid">
              <small class="form-text text-danger" v-if="!$v.category.description.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.sortOrder')" for="category-sortOrder"
              >Sort Order</label
            >
            <input
              type="number"
              class="form-control"
              name="sortOrder"
              id="category-sortOrder"
              data-cy="sortOrder"
              :class="{ valid: !$v.category.sortOrder.$invalid, invalid: $v.category.sortOrder.$invalid }"
              v-model.number="$v.category.sortOrder.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.dateAdded')" for="category-dateAdded"
              >Date Added</label
            >
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="category-dateAdded"
                  v-model="$v.category.dateAdded.$model"
                  name="dateAdded"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="category-dateAdded"
                data-cy="dateAdded"
                type="text"
                class="form-control"
                name="dateAdded"
                :class="{ valid: !$v.category.dateAdded.$invalid, invalid: $v.category.dateAdded.$invalid }"
                v-model="$v.category.dateAdded.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.dateModified')" for="category-dateModified"
              >Date Modified</label
            >
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="category-dateModified"
                  v-model="$v.category.dateModified.$model"
                  name="dateModified"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="category-dateModified"
                data-cy="dateModified"
                type="text"
                class="form-control"
                name="dateModified"
                :class="{ valid: !$v.category.dateModified.$invalid, invalid: $v.category.dateModified.$invalid }"
                v-model="$v.category.dateModified.$model"
              />
            </b-input-group>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.status')" for="category-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.category.status.$invalid, invalid: $v.category.status.$invalid }"
              v-model="$v.category.status.$model"
              id="category-status"
              data-cy="status"
            >
              <option
                v-for="categoryStatus in categoryStatusValues"
                :key="categoryStatus"
                v-bind:value="categoryStatus"
                v-bind:label="$t('jhipsterapp001App.CategoryStatus.' + categoryStatus)"
              >
                {{ categoryStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('jhipsterapp001App.category.parent')" for="category-parent">Parent</label>
            <select class="form-control" id="category-parent" data-cy="parent" name="parent" v-model="category.parent">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="category.parent && categoryOption.id === category.parent.id ? category.parent : categoryOption"
                v-for="categoryOption in categories"
                :key="categoryOption.id"
              >
                {{ categoryOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="$t('jhipsterapp001App.category.product')" for="category-product">Product</label>
            <select
              class="form-control"
              id="category-products"
              data-cy="product"
              multiple
              name="product"
              v-if="category.products !== undefined"
              v-model="category.products"
            >
              <option
                v-bind:value="getSelected(category.products, productOption)"
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.title }}
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
            :disabled="$v.category.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./category-update.component.ts"></script>
