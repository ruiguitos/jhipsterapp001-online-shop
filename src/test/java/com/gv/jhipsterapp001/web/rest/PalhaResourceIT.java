package com.gv.jhipsterapp001.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gv.jhipsterapp001.IntegrationTest;
import com.gv.jhipsterapp001.domain.Palha;
import com.gv.jhipsterapp001.repository.EntityManager;
import com.gv.jhipsterapp001.repository.PalhaRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PalhaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PalhaResourceIT {

    private static final String DEFAULT_JULIA = "AAAAAAAAAA";
    private static final String UPDATED_JULIA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/palhas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PalhaRepository palhaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Palha palha;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Palha createEntity(EntityManager em) {
        Palha palha = new Palha().julia(DEFAULT_JULIA);
        return palha;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Palha createUpdatedEntity(EntityManager em) {
        Palha palha = new Palha().julia(UPDATED_JULIA);
        return palha;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Palha.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        palha = createEntity(em);
    }

    @Test
    void createPalha() throws Exception {
        int databaseSizeBeforeCreate = palhaRepository.findAll().collectList().block().size();
        // Create the Palha
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeCreate + 1);
        Palha testPalha = palhaList.get(palhaList.size() - 1);
        assertThat(testPalha.getJulia()).isEqualTo(DEFAULT_JULIA);
    }

    @Test
    void createPalhaWithExistingId() throws Exception {
        // Create the Palha with an existing ID
        palha.setId(1L);

        int databaseSizeBeforeCreate = palhaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPalhas() {
        // Initialize the database
        palhaRepository.save(palha).block();

        // Get all the palhaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(palha.getId().intValue()))
            .jsonPath("$.[*].julia")
            .value(hasItem(DEFAULT_JULIA));
    }

    @Test
    void getPalha() {
        // Initialize the database
        palhaRepository.save(palha).block();

        // Get the palha
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, palha.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(palha.getId().intValue()))
            .jsonPath("$.julia")
            .value(is(DEFAULT_JULIA));
    }

    @Test
    void getNonExistingPalha() {
        // Get the palha
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPalha() throws Exception {
        // Initialize the database
        palhaRepository.save(palha).block();

        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();

        // Update the palha
        Palha updatedPalha = palhaRepository.findById(palha.getId()).block();
        updatedPalha.julia(UPDATED_JULIA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPalha.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPalha))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
        Palha testPalha = palhaList.get(palhaList.size() - 1);
        assertThat(testPalha.getJulia()).isEqualTo(UPDATED_JULIA);
    }

    @Test
    void putNonExistingPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, palha.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePalhaWithPatch() throws Exception {
        // Initialize the database
        palhaRepository.save(palha).block();

        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();

        // Update the palha using partial update
        Palha partialUpdatedPalha = new Palha();
        partialUpdatedPalha.setId(palha.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPalha.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPalha))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
        Palha testPalha = palhaList.get(palhaList.size() - 1);
        assertThat(testPalha.getJulia()).isEqualTo(DEFAULT_JULIA);
    }

    @Test
    void fullUpdatePalhaWithPatch() throws Exception {
        // Initialize the database
        palhaRepository.save(palha).block();

        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();

        // Update the palha using partial update
        Palha partialUpdatedPalha = new Palha();
        partialUpdatedPalha.setId(palha.getId());

        partialUpdatedPalha.julia(UPDATED_JULIA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPalha.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPalha))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
        Palha testPalha = palhaList.get(palhaList.size() - 1);
        assertThat(testPalha.getJulia()).isEqualTo(UPDATED_JULIA);
    }

    @Test
    void patchNonExistingPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, palha.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPalha() throws Exception {
        int databaseSizeBeforeUpdate = palhaRepository.findAll().collectList().block().size();
        palha.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(palha))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Palha in the database
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePalha() {
        // Initialize the database
        palhaRepository.save(palha).block();

        int databaseSizeBeforeDelete = palhaRepository.findAll().collectList().block().size();

        // Delete the palha
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, palha.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Palha> palhaList = palhaRepository.findAll().collectList().block();
        assertThat(palhaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
