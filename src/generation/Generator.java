package generation;

import model.Position;

/** 新しいMazeに通路を生成する。各インスタンスの生成は一度限り。 */
public interface Generator {
    void generate(Position start);
}
