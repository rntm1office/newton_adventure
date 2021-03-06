/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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

import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.controls.Action;
import im.bci.jnuit.controls.ActionActivatedDetector;
import im.bci.jnuit.controls.Control;
import im.bci.jnuit.controls.Pointer;
import im.bci.jnuit.playn.controls.KeyControl;
import im.bci.jnuit.playn.controls.PlaynNuitControls;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import java.util.Arrays;
import java.util.List;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import playn.core.Key;
import playn.core.PlayN;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynGameInput extends AbstractGameInput {

    private final BackButtonControl backButtonControl = new BackButtonControl();
    private final PlaynNuitControls controls;
    private final im.bci.jnuit.controls.Pointer pointer = new Pointer();
    private final VirtualPad virtualPad;

    public PlaynGameInput(NuitToolkit toolkit, NuitPreferences config, PlaynNuitControls controls, VirtualPad virtualpad) {
        super(toolkit, config);
        this.controls = controls;
        this.virtualPad = virtualpad;
    }

    @Override
    public ROVector2f getMousePos() {
        controls.pollPointer(PlayN.graphics().width(), PlayN.graphics().height(), pointer);
        pointer.setY(PlayN.graphics().height() - pointer.getY());
        return new Vector2f(pointer.getX(), pointer.getY());
    }

    @Override
    public boolean isMouseButtonDown() {
        controls.pollPointer(PlayN.graphics().width(), PlayN.graphics().height(), pointer);
        return pointer.isDown();
    }

    void onBackPressed() {
        backButtonControl.value = 1f;
        PlayN.invokeLater(new Runnable() {

            @Override
            public void run() {
                backButtonControl.value = 0f;
            }
        });
    }

    private class BackButtonControl implements Control {

        private float value;

        @Override
        public String getControllerName() {
            return "Device";
        }

        @Override
        public String getName() {
            return "Back";
        }

        @Override
        public float getDeadZone() {
            return 0.1f;
        }

        @Override
        public float getValue() {
            return value;
        }

    }

    @Override
    protected void setupGameControls() {
        activate = new ActionActivatedDetector(new Action("action.activate", new KeyControl(controls, Key.DOWN)));
        jump = new ActionActivatedDetector(new Action("action.jump", new KeyControl(controls, Key.UP), virtualPad.getVirtualControlUp()));
        left = new ActionActivatedDetector(new Action("action.left", new KeyControl(controls, Key.LEFT), virtualPad.getVirtualControlLeft()));
        right = new ActionActivatedDetector(new Action("action.right", new KeyControl(controls, Key.RIGHT), virtualPad.getVirtualControlRight()));
        rotateClockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise", new KeyControl(controls, Key.C), virtualPad.getVirtualControlRotateClockwise()));
        rotateCounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise", new KeyControl(controls, Key.X), virtualPad.getVirtualControlRotateCounterClockwise()));
        rotate90Clockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise.90", new KeyControl(controls, Key.S)));
        rotate90CounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise.90", new KeyControl(controls, Key.D)));
        returnToMenu = new ActionActivatedDetector(new Action("action.returntomenu", new KeyControl(controls, Key.ESCAPE), backButtonControl));

        cheatActivateAll = new ActionActivatedDetector(new Action("cheat.activate.all", new KeyControl(controls, Key.F8)));
        cheatGetWorldMap = new ActionActivatedDetector(new Action("cheat.get.world.map", new KeyControl(controls, Key.F9)));
        cheatGetCompass = new ActionActivatedDetector(new Action("cheat.get.compass", new KeyControl(controls, Key.F10)));
        cheatGotoNextBonusLevel = new ActionActivatedDetector(new Action("cheat.goto.next.bonus.level", new KeyControl(controls, Key.F11)));
        cheatGotoNextLevel = new ActionActivatedDetector(new Action("cheat.goto.next.level", new KeyControl(controls, Key.F12)));
        cheatSetAllCompleted = new ActionActivatedDetector(new Action("cheat.set.all.completed", new KeyControl(controls, Key.F12)));
    }

    @Override
    public List<Action> getDefaultGameActionList() {
        return Arrays.asList(new Action("action.jump", new KeyControl(controls, Key.UP)), new Action("action.left", new KeyControl(controls, Key.LEFT)), new Action("action.right", new KeyControl(controls, Key.RIGHT)), new Action("action.rotate.clockwise", new KeyControl(controls, Key.C)), new Action("action.rotate.counterclockwise", new KeyControl(controls, Key.X)), new Action("action.rotate.clockwise.90", new KeyControl(controls, Key.S)), new Action("action.rotate.counterclockwise.90", new KeyControl(controls, Key.D)), new Action("action.activate", new KeyControl(controls, Key.DOWN)), new Action("action.returntomenu", new KeyControl(controls, Key.ESCAPE)));
    }

}
