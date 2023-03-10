<template>
  <div>
    <h2 id="page-heading" data-cy="CategoryHeading">
      <span v-text="$t('jhipsterapp001App.category.home.title')" id="category-heading">Categories</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('jhipsterapp001App.category.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'CategoryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-category"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('jhipsterapp001App.category.home.createLabel')"> Create a new Category </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && categories && categories.length === 0">
      <span v-text="$t('jhipsterapp001App.category.home.notFound')">No categories found</span>
    </div>
    <div class="table-responsive" v-if="categories && categories.length > 0">
      <table class="table table-striped" aria-describedby="categories">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('description')">
              <span v-text="$t('jhipsterapp001App.category.description')">Description</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('sortOrder')">
              <span v-text="$t('jhipsterapp001App.category.sortOrder')">Sort Order</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'sortOrder'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('dateAdded')">
              <span v-text="$t('jhipsterapp001App.category.dateAdded')">Date Added</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'dateAdded'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('dateModified')">
              <span v-text="$t('jhipsterapp001App.category.dateModified')">Date Modified</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'dateModified'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="$t('jhipsterapp001App.category.status')">Status</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('parent.id')">
              <span v-text="$t('jhipsterapp001App.category.parent')">Parent</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'parent.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories" :key="category.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CategoryView', params: { categoryId: category.id } }">{{ category.id }}</router-link>
            </td>
            <td>{{ category.description }}</td>
            <td>{{ category.sortOrder }}</td>
            <td>{{ category.dateAdded }}</td>
            <td>{{ category.dateModified }}</td>
            <td v-text="$t('jhipsterapp001App.CategoryStatus.' + category.status)">{{ category.status }}</td>
            <td>
              <div v-if="category.parent">
                <router-link :to="{ name: 'CategoryView', params: { categoryId: category.parent.id } }">{{
                  category.parent.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CategoryView', params: { categoryId: category.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CategoryEdit', params: { categoryId: category.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(category)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="jhipsterapp001App.category.delete.question" data-cy="categoryDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-category-heading" v-text="$t('jhipsterapp001App.category.delete.question', { id: removeId })">
          Are you sure you want to delete this Category?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-category"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeCategory()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="categories && categories.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./category.component.ts"></script>
