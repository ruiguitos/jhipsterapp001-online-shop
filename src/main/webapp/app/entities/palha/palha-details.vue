<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="palha">
        <h2 class="jh-entity-heading" data-cy="palhaDetailsHeading">
          <span v-text="$t('jhipsterapp001App.palha.detail.title')">Júlia Palha</span> {{ palha.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('jhipsterapp001App.palha.julia')">Julia</span>
          </dt>
          <dd>
            <span>{{ palha.julia }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="palha.id" :to="{ name: 'PalhaEdit', params: { palhaId: palha.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./palha-details.component.ts"></script>
