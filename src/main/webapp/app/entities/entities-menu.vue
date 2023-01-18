<template>
  <div>
    <b-dropdown-item to="/category">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.category')">Category</span>
    </b-dropdown-item>
    <b-dropdown-item to="/product">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.product')">Product</span>
    </b-dropdown-item>
    <b-dropdown-item to="/customer">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.customer')">Customer</span>
    </b-dropdown-item>
    <b-dropdown-item to="/address">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.address')">Address</span>
    </b-dropdown-item>
    <b-dropdown-item to="/wish-list">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.wishList')">Wish List</span>
    </b-dropdown-item>
    <b-dropdown-item to="/palha">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.palha')">Júlia Palha</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
