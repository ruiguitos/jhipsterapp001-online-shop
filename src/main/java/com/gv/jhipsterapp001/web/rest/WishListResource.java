package com.gv.jhipsterapp001.web.rest;

import com.gv.jhipsterapp001.domain.WishList;
import com.gv.jhipsterapp001.repository.WishListRepository;
import com.gv.jhipsterapp001.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.gv.jhipsterapp001.domain.WishList}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    private static final String ENTITY_NAME = "wishList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishListRepository wishListRepository;

    public WishListResource(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    /**
     * {@code POST  /wish-lists} : Create a new wishList.
     *
     * @param wishList the wishList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishList, or with status {@code 400 (Bad Request)} if the wishList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wish-lists")
    public Mono<ResponseEntity<WishList>> createWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishList);
        if (wishList.getId() != null) {
            throw new BadRequestAlertException("A new wishList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return wishListRepository
            .save(wishList)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/wish-lists/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /wish-lists/:id} : Updates an existing wishList.
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wish-lists/{id}")
    public Mono<ResponseEntity<WishList>> updateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to update WishList : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return wishListRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return wishListRepository
                    .save(wishList)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /wish-lists/:id} : Partial updates given fields of an existing wishList, field will ignore if it is null
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 404 (Not Found)} if the wishList is not found,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wish-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<WishList>> partialUpdateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to partial update WishList partially : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return wishListRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<WishList> result = wishListRepository
                    .findById(wishList.getId())
                    .map(existingWishList -> {
                        if (wishList.getTitle() != null) {
                            existingWishList.setTitle(wishList.getTitle());
                        }
                        if (wishList.getRestricted() != null) {
                            existingWishList.setRestricted(wishList.getRestricted());
                        }

                        return existingWishList;
                    })
                    .flatMap(wishListRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /wish-lists} : get all the wishLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishLists in body.
     */
    @GetMapping("/wish-lists")
    public Mono<List<WishList>> getAllWishLists() {
        log.debug("REST request to get all WishLists");
        return wishListRepository.findAll().collectList();
    }

    /**
     * {@code GET  /wish-lists} : get all the wishLists as a stream.
     * @return the {@link Flux} of wishLists.
     */
    @GetMapping(value = "/wish-lists", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<WishList> getAllWishListsAsStream() {
        log.debug("REST request to get all WishLists as a stream");
        return wishListRepository.findAll();
    }

    /**
     * {@code GET  /wish-lists/:id} : get the "id" wishList.
     *
     * @param id the id of the wishList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wish-lists/{id}")
    public Mono<ResponseEntity<WishList>> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        Mono<WishList> wishList = wishListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wishList);
    }

    /**
     * {@code DELETE  /wish-lists/:id} : delete the "id" wishList.
     *
     * @param id the id of the wishList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wish-lists/{id}")
    public Mono<ResponseEntity<Void>> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        return wishListRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
