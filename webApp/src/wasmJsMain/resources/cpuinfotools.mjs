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

async function getStorageEstimate() {
    try {
        const estimate = await navigator.storage?.estimate?.();
        if (Number.isSafeInteger(estimate?.quota) && Number.isSafeInteger(estimate?.usage)) {
            return estimate;
        }
    } catch {
        // Storage estimation is unavailable, e.g. blocked by the browser
    }
    return { quota: -1, usage: -1 };
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
