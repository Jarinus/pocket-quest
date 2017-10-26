export default class ArrayUtils {
    static chunk(array, length) {
        let chunks = [];
        let i = 0;
        let n = array.length;

        while (i < n) {
            chunks.push(array.slice(i, i += length));
        }

        return chunks;
    }
}
