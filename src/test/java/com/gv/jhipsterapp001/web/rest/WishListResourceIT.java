package com.gv.jhipsterapp001.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gv.jhipsterapp001.IntegrationTest;
import com.gv.jhipsterapp001.domain.WishList;
import com.gv.jhipsterapp001.repository.EntityManager;
import com.gv.jhipsterapp001.repository.WishListRepository;
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
 * Integration tests for the {@link WishListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WishListResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESTRICTED = false;
    private static final Boolean UPDATED_RESTRICTED = true;

    private static final String ENTITY_API_URL = "/api/wish-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private WishList wishList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createEntity(EntityManager em) {
        WishList wishList = new WishList().title(DEFAULT_TITLE).restricted(DEFAULT_RESTRICTED);
        return wishList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createUpdatedEntity(EntityManager em) {
        WishList wishList = new WishList().title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);
        return wishList;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(WishList.class).block();
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
        wishList = createEntity(em);
    }

    @Test
    void createWishList() throws Exception {
        int databaseSizeBeforeCreate = wishListRepository.findAll().collectList().block().size();
        // Create the WishList
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate + 1);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(DEFAULT_RESTRICTED);
    }

    @Test
    void createWishListWithExistingId() throws Exception {
        // Create the WishList with an existing ID
        wishList.setId(1L);

        int databaseSizeBeforeCreate = wishListRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = wishListRepository.findAll().collectList().block().size();
        // set the field null
        wishList.setTitle(null);

        // Create the WishList, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllWishListsAsStream() {
        // Initialize the database
        wishListRepository.save(wishList).block();

        List<WishList> wishListList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(WishList.class)
            .getResponseBody()
            .filter(wishList::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(wishListList).isNotNull();
        assertThat(wishListList).hasSize(1);
        WishList testWishList = wishListList.get(0);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(DEFAULT_RESTRICTED);
    }

    @Test
    void getAllWishLists() {
        // Initialize the database
        wishListRepository.save(wishList).block();

        // Get all the wishListList
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
            .value(hasItem(wishList.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].restricted")
            .value(hasItem(DEFAULT_RESTRICTED.booleanValue()));
    }

    @Test
    void getWishList() {
        // Initialize the database
        wishListRepository.save(wishList).block();

        // Get the wishList
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, wishList.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(wishList.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.restricted")
            .value(is(DEFAULT_RESTRICTED.booleanValue()));
    }

    @Test
    void getNonExistingWishList() {
        // Get the wishList
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingWishList() throws Exception {
        // Initialize the database
        wishListRepository.save(wishList).block();

        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();

        // Update the wishList
        WishList updatedWishList = wishListRepository.findById(wishList.getId()).block();
        updatedWishList.title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedWishList.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedWishList))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    void putNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, wishList.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.save(wishList).block();

        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.restricted(UPDATED_RESTRICTED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    void fullUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.save(wishList).block();

        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    void patchNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, wishList.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().collectList().block().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wishList))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWishList() {
        // Initialize the database
        wishListRepository.save(wishList).block();

        int databaseSizeBeforeDelete = wishListRepository.findAll().collectList().block().size();

        // Delete the wishList
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, wishList.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<WishList> wishListList = wishListRepository.findAll().collectList().block();
        assertThat(wishListList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
