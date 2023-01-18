package com.gv.jhipsterapp001.web.rest;

import com.gv.jhipsterapp001.domain.Palha;
import com.gv.jhipsterapp001.repository.PalhaRepository;
import com.gv.jhipsterapp001.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.gv.jhipsterapp001.domain.Palha}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PalhaResource {

    private final Logger log = LoggerFactory.getLogger(PalhaResource.class);

    private static final String ENTITY_NAME = "palha";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PalhaRepository palhaRepository;

    public PalhaResource(PalhaRepository palhaRepository) {
        this.palhaRepository = palhaRepository;
    }

    /**
     * {@code POST  /palhas} : Create a new palha.
     *
     * @param palha the palha to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new palha, or with status {@code 400 (Bad Request)} if the palha has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/palhas")
    public Mono<ResponseEntity<Palha>> createPalha(@RequestBody Palha palha) throws URISyntaxException {
        log.debug("REST request to save Palha : {}", palha);
        if (palha.getId() != null) {
            throw new BadRequestAlertException("A new palha cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return palhaRepository
            .save(palha)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/palhas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /palhas/:id} : Updates an existing palha.
     *
     * @param id the id of the palha to save.
     * @param palha the palha to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palha,
     * or with status {@code 400 (Bad Request)} if the palha is not valid,
     * or with status {@code 500 (Internal Server Error)} if the palha couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/palhas/{id}")
    public Mono<ResponseEntity<Palha>> updatePalha(@PathVariable(value = "id", required = false) final Long id, @RequestBody Palha palha)
        throws URISyntaxException {
        log.debug("REST request to update Palha : {}, {}", id, palha);
        if (palha.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, palha.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return palhaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return palhaRepository
                    .save(palha)
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
     * {@code PATCH  /palhas/:id} : Partial updates given fields of an existing palha, field will ignore if it is null
     *
     * @param id the id of the palha to save.
     * @param palha the palha to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated palha,
     * or with status {@code 400 (Bad Request)} if the palha is not valid,
     * or with status {@code 404 (Not Found)} if the palha is not found,
     * or with status {@code 500 (Internal Server Error)} if the palha couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/palhas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Palha>> partialUpdatePalha(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Palha palha
    ) throws URISyntaxException {
        log.debug("REST request to partial update Palha partially : {}, {}", id, palha);
        if (palha.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, palha.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return palhaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Palha> result = palhaRepository
                    .findById(palha.getId())
                    .map(existingPalha -> {
                        if (palha.getJulia() != null) {
                            existingPalha.setJulia(palha.getJulia());
                        }

                        return existingPalha;
                    })
                    .flatMap(palhaRepository::save);

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
     * {@code GET  /palhas} : get all the palhas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of palhas in body.
     */
    @GetMapping("/palhas")
    public Mono<ResponseEntity<List<Palha>>> getAllPalhas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Palhas");
        return palhaRepository
            .count()
            .zipWith(palhaRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /palhas/:id} : get the "id" palha.
     *
     * @param id the id of the palha to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the palha, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/palhas/{id}")
    public Mono<ResponseEntity<Palha>> getPalha(@PathVariable Long id) {
        log.debug("REST request to get Palha : {}", id);
        Mono<Palha> palha = palhaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(palha);
    }

    /**
     * {@code DELETE  /palhas/:id} : delete the "id" palha.
     *
     * @param id the id of the palha to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/palhas/{id}")
    public Mono<ResponseEntity<Void>> deletePalha(@PathVariable Long id) {
        log.debug("REST request to delete Palha : {}", id);
        return palhaRepository
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
