#import "UhfPlugin.h"
#import <uhf/uhf-Swift.h>

@implementation UhfPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftUhfPlugin registerWithRegistrar:registrar];
}
@end
