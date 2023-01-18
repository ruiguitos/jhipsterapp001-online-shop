<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="wishList">
        <h2 class="jh-entity-heading" data-cy="wishListDetailsHeading">
          <span v-text="$t('jhipsterapp001App.wishList.detail.title')">WishList</span> {{ wishList.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('jhipsterapp001App.wishList.title')">Title</span>
          </dt>
          <dd>
            <span>{{ wishList.title }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsterapp001App.wishList.restricted')">Restricted</span>
          </dt>
          <dd>
            <span>{{ wishList.restricted }}</span>
          </dd>
          <dt>
            <span v-text="$t('jhipsterapp001App.wishList.customer')">Customer</span>
          </dt>
          <dd>
            <div v-if="wishList.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: wishList.customer.id } }">{{
                wishList.customer.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="wishList.id" :to="{ name: 'WishListEdit', params: { wishListId: wishList.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./wish-list-details.component.ts"></script>
