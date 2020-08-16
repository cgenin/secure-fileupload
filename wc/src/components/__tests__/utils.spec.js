import {PDF} from '../utils';

describe('PDF', () => {

    it('test props', () => {
        expect(PDF.name).toBe('PDF');
        expect(PDF.mimetype).toBe('application/pdf');
    });

    it('test parse', () => {
        expect(PDF.parse('PDF')).toBe(true);
        expect(PDF.parse('JPG')).toBe(false);
        expect(PDF.parse(null)).toBe(false);
    });

    it('test parse', () => {
        expect(PDF.match('test.pdf')).toBe(true);
        expect(PDF.match('TEST.PDF')).toBe(true);
        expect(PDF.parse('test.jps')).toBe(false);
        expect(PDF.parse(null)).toBe(false);
    });

    it('test isPdf', () => {
        expect(PDF.isPDF()).toBe(true);
    });
    it('test isImage', () => {
        expect(PDF.isImage()).toBe(false);
    });
});