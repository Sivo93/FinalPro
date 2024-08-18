package com.example.myweb.board.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.example.myweb.board.dto.NoticeBoardDTO;
import com.example.myweb.board.entity.NoticeBoardEntity;
import com.example.myweb.board.entity.NoticeBoardFileEntity;
import com.example.myweb.board.entity.NoticeBoardLikeEntity;
import com.example.myweb.board.repository.NoticeBoardFileRepository;
import com.example.myweb.board.repository.NoticeBoardLikeRepository;
import com.example.myweb.board.repository.NoticeBoardRepository;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// DTO -> Entity
// Entity -> DTO

@Service
@RequiredArgsConstructor
public class NoticeBoardService {
	private final NoticeBoardRepository noticeBoardRepository;
	private final NoticeBoardFileRepository noticeBoardFileRepository;
	private final NoticeBoardLikeRepository noticeBoardLikeRepository;
	private final UserRepository userRepository;

	public void save(NoticeBoardDTO noticeBoardDTO) throws IllegalStateException, IOException {
		// UserEntity를 UserRepository를 통해 조회합니다.
		UserEntity userEntity = userRepository
				.findByLoginidAndNickname(noticeBoardDTO.getLoginid(), noticeBoardDTO.getNickname()).get();

		// 파일 첨부 여부에 따라 로직 분리
		if (noticeBoardDTO.getNoticeboardFile().isEmpty()) {
			// 첨부 파일 없음
			NoticeBoardEntity noticeBoardEntity = NoticeBoardEntity.toSaveEntity(noticeBoardDTO, userEntity);
			noticeBoardRepository.save(noticeBoardEntity);
		} else {
			// 첨부 파일 있음
			NoticeBoardEntity noticeBoardEntity = NoticeBoardEntity.toSaveFileEntity(noticeBoardDTO, userEntity);
			Long savedSeq = noticeBoardRepository.save(noticeBoardEntity).getSeq(); // 게시글의 seq
			NoticeBoardEntity noticeBoard = noticeBoardRepository.findById(savedSeq).get(); // 게시글의 정보를 가져옴
			for (MultipartFile noticeBoardFile : noticeBoardDTO.getNoticeboardFile()) {
				String originalFilename = noticeBoardFile.getOriginalFilename(); // 파일의 이름 가져옴
				String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 서버 저장용 이름 생성
				String savePath = new File("/src/main/resources/static/upload/").getAbsolutePath() + "/"
						+ storedFileName;

				File file = new File(savePath);
				file.getParentFile().mkdirs(); // 경로가 존재하지 않으면 생성
				noticeBoardFile.transferTo(file); // 파일 저장

				NoticeBoardFileEntity noticeBoardFileEntity = NoticeBoardFileEntity.toNoticeBoardFileEntity(noticeBoard,
						originalFilename, storedFileName);
				noticeBoardFileRepository.save(noticeBoardFileEntity); // 파일 정보 저장
			}
		}
	}
	



	@Transactional
	public List<NoticeBoardDTO> findAll() {
		List<NoticeBoardEntity> noticeBoardEntityList = noticeBoardRepository.findAll();
		List<NoticeBoardDTO> noticeBoardDTOList = new ArrayList<>();

		for (NoticeBoardEntity noticeBoardEntity : noticeBoardEntityList) {
			noticeBoardDTOList.add(NoticeBoardDTO.toNoticeBoardDTO(noticeBoardEntity));
		}

		return noticeBoardDTOList;
	}

	@Transactional
	public void incrementViews(Long seq) {
		noticeBoardRepository.incrementViews(seq);
	}

	@Transactional
	public NoticeBoardDTO findBySeq(Long seq) {
		Optional<NoticeBoardEntity> optionalNoticeBoardEntity = noticeBoardRepository.findById(seq);
		if (optionalNoticeBoardEntity.isPresent()) {
			NoticeBoardEntity noticeBoardEntity = optionalNoticeBoardEntity.get();
			NoticeBoardDTO noticeBoardDTO = NoticeBoardDTO.toNoticeBoardDTO(noticeBoardEntity);

			return noticeBoardDTO;
		} else {
			return null;
		}
	}

	public void delete(Long seq) {
		noticeBoardRepository.deleteById(seq);
	}
	
	@Transactional
	public NoticeBoardDTO update(NoticeBoardDTO noticeBoardDTO) {
	    // 기존 게시글을 데이터베이스에서 조회
	    Optional<NoticeBoardEntity> optionalBoard = noticeBoardRepository.findById(noticeBoardDTO.getSeq());
	    Optional<UserEntity> optionalUser = userRepository.findByLoginidAndNickname(noticeBoardDTO.getLoginid(), noticeBoardDTO.getNickname());

	    if (optionalBoard.isPresent() && optionalUser.isPresent()) {
	        NoticeBoardEntity noticeBoardEntity = optionalBoard.get();
	        UserEntity userEntity = optionalUser.get();

	        // 필요한 필드만 업데이트
	        noticeBoardEntity.setTitle(noticeBoardDTO.getTitle());
	        noticeBoardEntity.setContents(noticeBoardDTO.getContents());
	        noticeBoardEntity.setTag(noticeBoardDTO.getTag());

	        // 연관된 엔티티(댓글, 파일 등)는 건드리지 않음
	        // 추가적인 연관 엔티티 변경이 필요하면 적절히 처리 (add/remove)

	        // 업데이트된 엔티티 저장
	        noticeBoardRepository.save(noticeBoardEntity);

	        return NoticeBoardDTO.toNoticeBoardDTO(noticeBoardEntity);
	    } else {
	        return null;
	    }
	}



	
	@Transactional
    public Page<NoticeBoardDTO> paging(Pageable pageable, String tag) {
        int page = pageable.getPageNumber();
        if (page < 0) {
            page = 0;
        } else {
            page -= 1;
        }
        int pageLimit = 5; // 한 페이지에 보여줄 글 갯수

        Page<NoticeBoardEntity> noticeBoardEntities;
        
        if (tag == null || tag.isEmpty()) {
            // 태그가 없을 경우 전체 게시글을 가져옴
        	noticeBoardEntities = noticeBoardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));
        } else {
            // 태그가 있을 경우 해당 태그의 게시글을 가져옴
        	noticeBoardEntities = noticeBoardRepository.findByTag(tag, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "seq")));
        }

        Page<NoticeBoardDTO> noticeBoardDTOS = noticeBoardEntities.map(noticeBoard -> new NoticeBoardDTO(
    		noticeBoard.getSeq(),
    		noticeBoard.getTag(),
    		noticeBoard.getTitle(),
    		noticeBoard.getCreatedTime(),
    		noticeBoard.getViews(),
    		noticeBoard.getLikeCount(),
    		noticeBoard.getNickname()
        ));

        return noticeBoardDTOS;
    }

	// 좋아요 기능
	@Transactional
	public boolean toggleLike(Long noticeBoard_seq, String loginid) {
		// 게시글과 사용자 정보를 Optional로 조회합니다.
		Optional<NoticeBoardEntity> optionalBoard = noticeBoardRepository.findById(noticeBoard_seq);
		Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);

		// Optional에서 데이터를 가져올 수 있는지 확인합니다.
		if (optionalBoard.isPresent() && optionalUser.isPresent()) {
			NoticeBoardEntity board = optionalBoard.get();
			UserEntity user = optionalUser.get();

			// 좋아요 여부를 확인하기 위해 좋아요 엔티티를 조회합니다.
			Optional<NoticeBoardLikeEntity> optionalLike = noticeBoardLikeRepository.findByUserAndNoticeBoardEntity(user,
					board);

			if (optionalLike.isPresent()) {
				// 이미 좋아요를 누른 상태이면 좋아요 취소 처리합니다.
				NoticeBoardLikeEntity like = optionalLike.get();
				noticeBoardLikeRepository.delete(like); // 좋아요 엔티티 삭제
				board.setLikeCount(board.getLikeCount() - 1); // 게시글의 좋아요 수 감소
			} else {
				// 좋아요를 누르지 않은 상태이면 좋아요 추가 처리합니다.
				NoticeBoardLikeEntity like = new NoticeBoardLikeEntity();
				like.setUser(user);
				like.setNoticeBoardEntity(board);
				noticeBoardLikeRepository.save(like); // 좋아요 엔티티 저장
				board.setLikeCount(board.getLikeCount() + 1); // 게시글의 좋아요 수 증가
			}

			// 게시글 엔티티 저장 (좋아요 수 변경 반영)
			noticeBoardRepository.save(board);

			return true; // 성공적으로 처리됨을 반환
		}

		return false; // 게시글 또는 사용자가 존재하지 않음을 반환
	}
	public boolean isLikedByUser(Long boardSeq, String loginid) {
		Optional<NoticeBoardEntity> optionalBoard = noticeBoardRepository.findById(boardSeq);
		Optional<UserEntity> optionalUser = userRepository.findByLoginid(loginid);
		if (optionalBoard.isPresent() && optionalUser.isPresent()) {
			NoticeBoardEntity board = optionalBoard.get();
			UserEntity user = optionalUser.get();
			return noticeBoardLikeRepository.findByUserAndNoticeBoardEntity(user, board).isPresent();
		}
		return false;
	}

	private static final String UPLOAD_DIR = "C:/upload/"; // 새로운 파일 저장 위치

	public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        // 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        String filePath = UPLOAD_DIR + storedFileName;

        // 파일 저장
        File dest = new File(filePath);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // FileEntity 생성 및 저장
        NoticeBoardFileEntity fileEntity = new NoticeBoardFileEntity();
        fileEntity.setOriginalFileName(originalFileName);
        fileEntity.setStoredFileName(storedFileName);
        // assuming you have a repository for saving file entities
        noticeBoardFileRepository.save(fileEntity);

        // 이미지 URL 반환
        return "/upload/" + storedFileName;
    }
	
	// 추천수 많은 3개 가져오기
    public List<NoticeBoardDTO> getTop3PopularPosts() {
        List<NoticeBoardEntity> popularPosts = noticeBoardRepository.findTop3ByOrderByLikeCountDesc();
        return popularPosts.stream()
                           .map(NoticeBoardDTO::toNoticeBoardDTO)
                           .toList();
    }
	

}