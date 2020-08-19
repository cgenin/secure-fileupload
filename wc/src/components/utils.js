export function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        const r = Math.random() * 16 | 0;
        const v = (c === 'x') ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

export function isXhr2() {
    const xhr = new XMLHttpRequest();
    return (xhr.upload);
}

export class Extension {
    constructor(name, extensions, mimetype) {
        this.name = name;
        this.extensions = extensions;
        this.mimetype = mimetype;
    }

    parse(item) {
        if (!item) {
            return false;
        }
        return item.trim().toUpperCase() === this.name;
    }

    match(filename) {
        return this.extensions
            .map(e => new RegExp(`\\.${e}$`, 'gi'))
            .findIndex(r => r.test(filename)) > -1;
    }

    isImage() {
        return this.extensions
            .findIndex(e => /GIF|JPG|PNG|JPEG/.test(e)) > -1;
    }

    isPDF() {
        return this.name === PDF.name;
    }
}

export const PDF = new Extension('PDF', ['PDF'], ['application/pdf']);
export const JPG = new Extension('JPG', ['JPG', 'JPEG'], ['image/jpg']);
export const PNG = new Extension('PNG', ['PNG'], ['image/png']);
export const WORD = new Extension('WORD', ['DOC', 'DOCX'], ['application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document']);

export const ALL_EXTENSIONS = [PDF, PNG, JPG];

export function convert2Extensions(names) {
    console.log(names)
    return (names || [])
        .map(n => {
            return ALL_EXTENSIONS.find(e => e.parse(n));
        })
        .filter(e => (e));
}

export function accept(extensions) {
    return extensions
        .flatMap(e => e.mimetype)
        .reduce((acc, e) => {
            if (acc.length === 0) {
                return e;
            }
            return `${acc},${e}`;
        }, '');
}

export function createEndPoint(url, token) {
    if (url) {
        return url;
    }
    if (token) {
        return `/upload/${token}`
    }
    throw new Error('Token or url are required to send data');
}

