<template>
  <div>
    <h2 id="page-heading" data-cy="WishListHeading">
      <span v-text="$t('jhipsterapp001App.wishList.home.title')" id="wish-list-heading">Wish Lists</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('jhipsterapp001App.wishList.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'WishListCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-wish-list"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('jhipsterapp001App.wishList.home.createLabel')"> Create a new Wish List </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && wishLists && wishLists.length === 0">
      <span v-text="$t('jhipsterapp001App.wishList.home.notFound')">No wishLists found</span>
    </div>
    <div class="table-responsive" v-if="wishLists && wishLists.length > 0">
      <table class="table table-striped" aria-describedby="wishLists">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('jhipsterapp001App.wishList.title')">Title</span></th>
            <th scope="row"><span v-text="$t('jhipsterapp001App.wishList.restricted')">Restricted</span></th>
            <th scope="row"><span v-text="$t('jhipsterapp001App.wishList.customer')">Customer</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="wishList in wishLists" :key="wishList.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'WishListView', params: { wishListId: wishList.id } }">{{ wishList.id }}</router-link>
            </td>
            <td>{{ wishList.title }}</td>
            <td>{{ wishList.restricted }}</td>
            <td>
              <div v-if="wishList.customer">
                <router-link :to="{ name: 'CustomerView', params: { customerId: wishList.customer.id } }">{{
                  wishList.customer.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'WishListView', params: { wishListId: wishList.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'WishListEdit', params: { wishListId: wishList.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(wishList)"
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
        ><span id="jhipsterapp001App.wishList.delete.question" data-cy="wishListDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-wishList-heading" v-text="$t('jhipsterapp001App.wishList.delete.question', { id: removeId })">
          Are you sure you want to delete this Wish List?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-wishList"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeWishList()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./wish-list.component.ts"></script>
