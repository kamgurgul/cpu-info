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
