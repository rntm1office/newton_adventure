/*
 * Copyright (c) 2014 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.platform.playn.core;

import playn.core.gl.GLContext;
import playn.core.gl.IndexedTrisShader;

/**
 *
 * @author devnewton
 *
 * Inspired by
 * http://blog.noblemaster.com/2013/10/26/opengl-shader-to-correct-and-simulate-color-blindness-experimental/
 *
 */
public class DaltonShader extends IndexedTrisShader {

    public enum BlindnessType {

        PROTANOPIA,
        DEUTERANOPIA,
        TRITANOPIA
    }

    private BlindnessType blindnessFilter = BlindnessType.TRITANOPIA;
    private BlindnessType blindnessVision = BlindnessType.TRITANOPIA;

    public DaltonShader(GLContext ctx, BlindnessType blindnessFilter, BlindnessType blindnessVision) {
        super(ctx);
        this.blindnessFilter = blindnessFilter;
        this.blindnessVision = blindnessVision;
    }

    @Override
    protected String textureFragmentShader() {
        String blindnessFilterData = "";
        String blindnessFilterCode = "";
        if (blindnessFilter != null) {
            switch (blindnessFilter) {
                case PROTANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= opponentColor.y * 1.5;                                           \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                case DEUTERANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= opponentColor.y * 1.5;                                           \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                case TRITANOPIA: {
                    blindnessFilterCode = "vec3 opponentColor = RGBtoOpponentMat * vec3(fragColor.r, fragColor.g, fragColor.b);\n"
                            + "opponentColor.x -= ((3.0 * opponentColor.z) - opponentColor.y) * 0.25;                \n"
                            + "vec3 rgbColor = OpponentToRGBMat * opponentColor;                                   \n"
                            + "fragColor = vec4(rgbColor.r, rgbColor.g, rgbColor.b, fragColor.a);                  \n";
                    break;
                }
                default:
                    throw new RuntimeException("Color filter not implemented for " + blindnessFilter);
            }
            blindnessFilterData = "const mat3 RGBtoOpponentMat = mat3(0.2814, -0.0971, -0.0930, 0.6938, 0.1458,-0.2529, 0.0638, -0.0250, 0.4665);\n"
                    + "const mat3 OpponentToRGBMat = mat3(1.1677, 0.9014, 0.7214, -6.4315, 2.5970, 0.1257, -0.5044, 0.0159, 2.0517);\n";
        }
        String blindnessVisionData = "";
        String blindnessVisionCode = "";
        if (blindnessVision != null) {
            switch (blindnessVision) {
                case PROTANOPIA: {
                    blindnessVisionData = "const vec4 blindVisionR = vec4( 0.20,  0.99, -0.19, 0.0);\n"
                            + "const vec4 blindVisionG = vec4( 0.16,  0.79,  0.04, 0.0);\n"
                            + "const vec4 blindVisionB = vec4( 0.01, -0.01,  1.00, 0.0);\n";
                    break;
                }
                case DEUTERANOPIA: {
                    blindnessVisionData = "const vec4 blindVisionR = vec4( 0.43,  0.72, -0.15, 0.0 );\n"
                            + "const vec4 blindVisionG = vec4( 0.34,  0.57,  0.09, 0.0 );\n"
                            + "const vec4 blindVisionB = vec4(-0.02,  0.03,  1.00, 0.0 );\n";
                    break;
                }
                case TRITANOPIA: {
                    blindnessVisionData = "const vec4 blindVisionR = vec4( 0.97,  0.11, -0.08, 0.0 );\n"
                            + "const vec4 blindVisionG = vec4( 0.02,  0.82,  0.16, 0.0 );\n"
                            + "const vec4 blindVisionB = vec4(-0.06,  0.88,  0.18, 0.0 );\n";
                    break;
                }
                default:
                    throw new RuntimeException("Color vision not implemented for " + blindnessVision);
            }
            blindnessVisionCode = "fragColor = vec4(dot(fragColor, blindVisionR), dot(fragColor, blindVisionG), dot(fragColor, blindVisionB), fragColor.a);\n";
        }
        String program
                = "#ifdef GL_ES                                          \n"
                + "precision mediump float;                              \n"
                + "#endif                                                \n"
                + "varying vec2 v_TexCoord;                              \n"
                + "varying vec4 v_Color;                                 \n"
                + "uniform sampler2D u_Texture;                          \n"
                + blindnessFilterData
                + blindnessVisionData
                + "void main()                                           \n"
                + "{                                                     \n"
                + "  vec4 fragColor = texture2D(u_Texture, v_TexCoord);  \n"
                + "  fragColor *= v_Color;                               \n"
                + blindnessFilterCode
                + blindnessVisionCode
                + "  gl_FragColor = fragColor;                           \n"
                + "}                                                     \n";

        return program;
    }

}
