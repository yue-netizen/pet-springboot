<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import Layout from './components/Layout.vue'

const route = useRoute()
</script>

<template>
  <Layout v-if="!route.meta.noLayout">
    <RouterView />
  </Layout>
  <RouterView v-else />
</template>
