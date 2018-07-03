#pragma version(1)
#pragma rs java_package_name(jp.ac.titech.itpro.sdl.activitytest2)
#pragma rs_fp_relaxed

float light = 0.f;
float MAX = 255;

/*
 * RenderScript kernel that performs saturation manipulation.
 */
uchar4 __attribute__((kernel)) saturation(uchar4 in)
{
    float4 f4 = rsUnpackColor8888(in);
    float red = min(f4.r + light,MAX);
    float blue = min(f4.b + light,MAX);
    float green = min(f4.g + light,MAX);

    return rsPackColorTo8888(red, green, blue, f4.a);
}
