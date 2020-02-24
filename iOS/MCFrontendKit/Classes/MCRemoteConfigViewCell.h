//
//  MCRemoteConfigViewCell.h
//  MCFrontendKit
//
//  Created by Rake Yang on 2020/2/23.
//

#import <UIKit/UIKit.h>
#import <MCUIKit/MCUIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface MCRemoteConfigViewCell : UITableViewCell

@property (nonatomic, strong) UILabel *extraLabel;

@property (nonatomic, assign) BOOL checked;

+ (CGFloat)heightForObject:(id)obj;

@end

NS_ASSUME_NONNULL_END
