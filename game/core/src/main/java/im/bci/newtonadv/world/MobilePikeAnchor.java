/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.world;

import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.Play;
import im.bci.newtonadv.game.AbstractDrawableStaticBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Shape;

/**
 *
 * @author devnewton
 */
public class MobilePikeAnchor extends AbstractDrawableStaticBody implements Updatable {

    private static final float radius = World.distanceUnit;
    private Play play;
    private final World world;

    MobilePikeAnchor(World world) {
        this(world,new Circle(radius));
    }

    public MobilePikeAnchor(World world, Shape shape) {
        super(shape);
        setFriction(10.0f);
        addBit(World.STATIC_BODY_COLLIDE_BIT);
        this.world = world;
	}

	public void setTexture(AnimationCollection texture) {
        this.play = texture.getFirst().start();
    }

    @Override
    public void draw() {
        world.getView().drawMobilePikeAnchor(this,play.getCurrentFrame());
    }

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		play.update(frameTimeInfos.elapsedTime / 1000000);		
	}
}
