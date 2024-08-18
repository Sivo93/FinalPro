package com.example.myweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.myweb.board.dto.NoticeBoardCommentDTO;
import com.example.myweb.board.entity.NoticeBoardCommentEntity;
import com.example.myweb.board.entity.NoticeBoardEntity;
import com.example.myweb.board.repository.NoticeBoardCommentRepository;
import com.example.myweb.board.repository.NoticeBoardRepository;
import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeBoardCommentService {
	private final NoticeBoardCommentRepository noticeBoardCommentRepository;
	private final NoticeBoardRepository noticeBoardRepository;
	private final UserRepository userRepository;
	private final HttpSession session;

	public Long save(NoticeBoardCommentDTO noticeBoardCommentDTO) {
		// 세션에서 loginid와 nickname 가져오기
		String loginid = (String) session.getAttribute("loginid");
		String nickname = (String) session.getAttribute("nickname");

		if (loginid == null || nickname == null) {
			return null; // 로그인 정보가 없으면 null 반환
		}

		Optional<UserEntity> optionalUserEntity = userRepository.findByLoginidAndNickname(loginid, nickname);
		if (!optionalUserEntity.isPresent()) {
			return null; // 사용자가 존재하지 않으면 null 반환
		}

		UserEntity userEntity = optionalUserEntity.get();
		Optional<NoticeBoardEntity> optionalNoticeBoardEntity = noticeBoardRepository
				.findById(noticeBoardCommentDTO.getNoticeBoardSeq());
		if (optionalNoticeBoardEntity.isPresent()) {
			NoticeBoardEntity noticeBoardEntity = optionalNoticeBoardEntity.get();
			NoticeBoardCommentEntity noticeBoardCommentEntity = NoticeBoardCommentEntity.toSaveEntity(noticeBoardCommentDTO,
					noticeBoardEntity, userEntity);
			return noticeBoardCommentRepository.save(noticeBoardCommentEntity).getSeq();
		} else {
			return null;
		}
	}

	@Transactional
	public List<NoticeBoardCommentDTO> findAll(Long noticeBoardSeq) {
		// select * from notice_board_comment_table where notice_board_seq=? order by id
		// desc;
		NoticeBoardEntity noticeBoardEntity = noticeBoardRepository.findById(noticeBoardSeq).get();
		List<NoticeBoardCommentEntity> noticeBoardCommentEntitiyList = noticeBoardCommentRepository
				.findAllByNoticeBoardEntityOrderBySeqDesc(noticeBoardEntity);

		// EntityList => DTOList
		List<NoticeBoardCommentDTO> noticeBoardCommentDTOList = new ArrayList<>();
		for (NoticeBoardCommentEntity noticeBoardCommentEntity : noticeBoardCommentEntitiyList) {
			NoticeBoardCommentDTO noticeBoardCommentDTO = NoticeBoardCommentDTO.toNoticeBoardCommentDTO(noticeBoardCommentEntity);
			noticeBoardCommentDTOList.add(noticeBoardCommentDTO);
		}

		return noticeBoardCommentDTOList;
	}

	// 댓글 수정 메서드
	@Transactional
	public Long updateComment(Long commentSeq, String newContent) {
		Optional<NoticeBoardCommentEntity> optionalComment = noticeBoardCommentRepository.findById(commentSeq);

		if (optionalComment.isPresent()) {
			NoticeBoardCommentEntity comment = optionalComment.get();
			comment.setCommentContentes(newContent); // 댓글 내용 수정
			noticeBoardCommentRepository.save(comment); // 수정된 댓글 저장
			return comment.getSeq(); // 수정된 댓글의 시퀀스 반환
		}

		return null; // 댓글이 존재하지 않는 경우 null 반환
	}

	public boolean deleteComment(Long commentSeq) {
		try {
			System.out.println("commentSeq:" + commentSeq);
			if (noticeBoardCommentRepository.existsById(commentSeq)) {
				noticeBoardCommentRepository.deleteById(commentSeq);
				return true; // 삭제 성공
			} else {
				return false; // 댓글이 존재하지 않음
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false; // 삭제 실패
		}
	}

}