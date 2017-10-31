export default class ArrayUtils {
    static chunk(array, length) {
        if (length <= 0) {
            return array
        }

        let chunks = [];
        let i = 0;
        let n = array.length;

        while (i < n) {
            chunks.push(array.slice(i, i += length));
        }

        return chunks;
    }
}
