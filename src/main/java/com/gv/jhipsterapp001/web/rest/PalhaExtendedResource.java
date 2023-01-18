// scr/main/java/web/rest/PalhaExtendedResource

package com.gv.jhipsterapp001.web.rest;

import com.gv.jhipsterapp001.domain.Palha;
import com.gv.jhipsterapp001.repository.PalhaRepository;
import com.gv.jhipsterapp001.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing {@link Palha}.
 */
@RestController
@RequestMapping("/api/v1")
@Transactional
public class PalhaExtendedResource {

    private final Logger log = LoggerFactory.getLogger(PalhaExtendedResource.class);

    private static final String ENTITY_NAME = "palha";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PalhaRepository palhaRepository;

    public PalhaExtendedResource(PalhaRepository palhaRepository) {
        this.palhaRepository = palhaRepository;
    }


    /**
     * {@code GET  /palhas} : get all the palhas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of palhas in body.
     */
    @GetMapping("/palhas/boobs")
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


}
