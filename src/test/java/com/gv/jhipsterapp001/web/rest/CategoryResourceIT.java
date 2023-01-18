package com.gv.jhipsterapp001.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.gv.jhipsterapp001.IntegrationTest;
import com.gv.jhipsterapp001.domain.Category;
import com.gv.jhipsterapp001.domain.enumeration.CategoryStatus;
import com.gv.jhipsterapp001.repository.CategoryRepository;
import com.gv.jhipsterapp001.repository.EntityManager;
import com.gv.jhipsterapp001.service.CategoryService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CategoryResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());

    private static final CategoryStatus DEFAULT_STATUS = CategoryStatus.AVAILABLE;
    private static final CategoryStatus UPDATED_STATUS = CategoryStatus.RESTRICTED;

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @Mock
    private CategoryService categoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Category category;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createEntity(EntityManager em) {
        Category category = new Category()
            .description(DEFAULT_DESCRIPTION)
            .sortOrder(DEFAULT_SORT_ORDER)
            .dateAdded(DEFAULT_DATE_ADDED)
            .dateModified(DEFAULT_DATE_MODIFIED)
            .status(DEFAULT_STATUS);
        return category;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Category createUpdatedEntity(EntityManager em) {
        Category category = new Category()
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED)
            .status(UPDATED_STATUS);
        return category;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_category__product").block();
            em.deleteAll(Category.class).block();
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
        category = createEntity(em);
    }

    @Test
    void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().collectList().block().size();
        // Create the Category
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testCategory.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
        assertThat(testCategory.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createCategoryWithExistingId() throws Exception {
        // Create the Category with an existing ID
        category.setId(1L);

        int databaseSizeBeforeCreate = categoryRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = categoryRepository.findAll().collectList().block().size();
        // set the field null
        category.setDescription(null);

        // Create the Category, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCategories() {
        // Initialize the database
        categoryRepository.save(category).block();

        // Get all the categoryList
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
            .value(hasItem(category.getId().intValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].sortOrder")
            .value(hasItem(DEFAULT_SORT_ORDER))
            .jsonPath("$.[*].dateAdded")
            .value(hasItem(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.[*].dateModified")
            .value(hasItem(DEFAULT_DATE_MODIFIED.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCategoriesWithEagerRelationshipsIsEnabled() {
        when(categoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(categoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCategoriesWithEagerRelationshipsIsNotEnabled() {
        when(categoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(categoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCategory() {
        // Initialize the database
        categoryRepository.save(category).block();

        // Get the category
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, category.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(category.getId().intValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.sortOrder")
            .value(is(DEFAULT_SORT_ORDER))
            .jsonPath("$.dateAdded")
            .value(is(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.dateModified")
            .value(is(DEFAULT_DATE_MODIFIED.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingCategory() {
        // Get the category
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category).block();

        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();

        // Update the category
        Category updatedCategory = categoryRepository.findById(category.getId()).block();
        updatedCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCategory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        assertThat(testCategory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.save(category).block();

        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());

        partialUpdatedCategory.description(UPDATED_DESCRIPTION).dateModified(UPDATED_DATE_MODIFIED).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCategory.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testCategory.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        assertThat(testCategory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateCategoryWithPatch() throws Exception {
        // Initialize the database
        categoryRepository.save(category).block();

        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();

        // Update the category using partial update
        Category partialUpdatedCategory = new Category();
        partialUpdatedCategory.setId(category.getId());

        partialUpdatedCategory
            .description(UPDATED_DESCRIPTION)
            .sortOrder(UPDATED_SORT_ORDER)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCategory.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testCategory.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testCategory.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        assertThat(testCategory.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, category.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().collectList().block().size();
        category.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(category))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCategory() {
        // Initialize the database
        categoryRepository.save(category).block();

        int databaseSizeBeforeDelete = categoryRepository.findAll().collectList().block().size();

        // Delete the category
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, category.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Category> categoryList = categoryRepository.findAll().collectList().block();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
