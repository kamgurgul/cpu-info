//
//  CpuInfoFramework.m
//  CpuInfoFramework
//
//  Created by kgurgul on 02/07/2024.
//  Copyright © 2024 KG Soft. All rights reserved.
//

#import <CpuInfoFramework/CpuInfoFramework.h>
#import <CpuInfoFramework-Swift.h>

@implementation CpuInfoFramework

- (long)getAvailableMemory {
    HardwareDataProvider *hardwareDataProvider = [[HardwareDataProvider alloc] init];
    return hardwareDataProvider.getAvailableMemory;
}

@end
