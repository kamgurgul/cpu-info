//
//  CpuInfoFramework.h
//  CpuInfoFramework
//
//  Created by kgurgul on 02/07/2024.
//  Copyright © 2024 KG Soft. All rights reserved.
//

#import <Foundation/Foundation.h>

//! Project version number for CpuInfoFramework.
FOUNDATION_EXPORT double CpuInfoFrameworkVersionNumber;

//! Project version string for CpuInfoFramework.
FOUNDATION_EXPORT const unsigned char CpuInfoFrameworkVersionString[];

@interface CpuInfoFramework : NSObject

- (long)getAvailableMemory;

@end
