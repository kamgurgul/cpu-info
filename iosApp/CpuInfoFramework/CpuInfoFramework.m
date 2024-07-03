//
//  CpuInfoFramework.m
//  CpuInfoFramework
//
//  Created by kgurgul on 02/07/2024.
//  Copyright © 2024 KG Soft. All rights reserved.
//

#import <CpuInfoFramework/CpuInfoFramework.h>
#import <CpuInfoFramework-Swift.h>

const long getAvailableMemory(void) {
    return [HardwareDataProvider.sharedInstance getAvailableMemory];
}
