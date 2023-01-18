/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import { DATE_FORMAT } from '@/shared/date/filters';
import CategoryService from '@/entities/category/category.service';
import { Category } from '@/shared/model/category.model';
import { CategoryStatus } from '@/shared/model/enumerations/category-status.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Category Service', () => {
    let service: CategoryService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new CategoryService();
      currentDate = new Date();
      elemDefault = new Category(123, 'AAAAAAA', 0, currentDate, currentDate, CategoryStatus.AVAILABLE);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            dateAdded: dayjs(currentDate).format(DATE_FORMAT),
            dateModified: dayjs(currentDate).format(DATE_FORMAT),
          },
          elemDefault
        );
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Category', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            dateAdded: dayjs(currentDate).format(DATE_FORMAT),
            dateModified: dayjs(currentDate).format(DATE_FORMAT),
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            dateAdded: currentDate,
            dateModified: currentDate,
          },
          returnedFromService
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Category', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Category', async () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            sortOrder: 1,
            dateAdded: dayjs(currentDate).format(DATE_FORMAT),
            dateModified: dayjs(currentDate).format(DATE_FORMAT),
            status: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateAdded: currentDate,
            dateModified: currentDate,
          },
          returnedFromService
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Category', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Category', async () => {
        const patchObject = Object.assign(
          {
            sortOrder: 1,
            dateModified: dayjs(currentDate).format(DATE_FORMAT),
          },
          new Category()
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            dateAdded: currentDate,
            dateModified: currentDate,
          },
          returnedFromService
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Category', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Category', async () => {
        const returnedFromService = Object.assign(
          {
            description: 'BBBBBB',
            sortOrder: 1,
            dateAdded: dayjs(currentDate).format(DATE_FORMAT),
            dateModified: dayjs(currentDate).format(DATE_FORMAT),
            status: 'BBBBBB',
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            dateAdded: currentDate,
            dateModified: currentDate,
          },
          returnedFromService
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Category', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Category', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Category', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
