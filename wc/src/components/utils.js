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

class Extension {
    constructor(name, extensions, mimetype) {
        this.name = name;
        this.extensions = extensions;
        this.mimetype = mimetype;
    }

    parse(item) {
        console.log(this.name)
        return item || item.toUpperCase() === this.name;
    }

    match(filename) {
        return this.extensions
            .map(e => new RegExp(`\\.${e}$`, 'gi'))
            .find(r => r.test(filename));
    }

    isImage() {
        return this.extensions
            .find(e => /GIF|JPG|PNG|JPEG/.test(e));
    }

    isPDF() {
        return this.name === PDF.name;
    }
}

export const PDF = new Extension('PDF', ['PDF'], 'application/pdf');
export const JPG = new Extension('JPG', ['JPG', 'JPEG'], 'image/jpg');
export const PNG = new Extension('PNG', ['PNG'], 'image/png');

export const ALL_EXTENSIONS = [PDF, PNG, JPG];
export const ALL_NAMES = ALL_EXTENSIONS.map(e => e.name);

export function convert2Extensions(names) {
    return (names || [])
        .map(n => {
            console.log(n)
           return  ALL_EXTENSIONS.find(e => e.parse(n));
        })
        .filter(e => (e));
}

export function accept(extensions) {
    return extensions
        .map(e => e.mimetype)
        .reduce((acc, e) => {
            if (acc.length === 0) {
                return e;
            }
            return `${acc},${e}`;
        }, '');
}

