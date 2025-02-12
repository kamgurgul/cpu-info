function getUsedHeapSize() {
    if (performance && performance.memory) {
        return performance.memory.usedJSHeapSize;
    } else {
        return -1;
    }
}

function getTotalHeapSize() {
    if (performance && performance.memory) {
        return performance.memory.totalJSHeapSize;
    } else {
        return -1;
    }
}

async function getTotalStorage() {
    if ("storage" in navigator && "estimate" in navigator.storage) {
        const { usage, quota } = await navigator.storage.estimate();
        return BigInt(quota);
    } else {
        return BigInt(-1);
    }
}

async function getUsedStorage() {
    if ("storage" in navigator && "estimate" in navigator.storage) {
        const { usage, quota } = await navigator.storage.estimate();
        return BigInt(usage);
    } else {
        return BigInt(-1);
    }
}

function isPdfViewerEnabled() {
    if ("pdfViewerEnabled" in navigator) {
        return navigator.pdfViewerEnabled;
    } else {
        return false;
    }
}

function jsNormalize(value) {
    return value.normalize("NFD")
}
